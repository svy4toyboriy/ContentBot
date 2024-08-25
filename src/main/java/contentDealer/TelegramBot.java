package contentDealer;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "TheContentDealerBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "6754183310:AAGoPbkWc1nZkyHHUM8bjJ_V0_60nigu9IA";
    }

    private static final int RESULTS_AMOUNT = 5;
    public static String format = "m4a";
    public static String addition = " audio";
    public static int language = 0;
    public static long CHAT_ID;
    public static String[][] phrases = {{"Отлично! Выберите формат /audio или /video. Изменить его можно будет в любой момент.",
            "Выберите аудио:", "Скачиваю...",
            "Загружаю...", "А вот и оно!", "Формат изменён на аудио. Введите свой запрос!",
            "Формат изменён на видео. Введите свой запрос!", "Недоступно. Выберите другую кнопку.", "Выберите видео:"},
            {"Alright! Choose format /audio or /video. You'll be able to change it anytime.", "Choose audio:", "Downloading...",
            "Uploading...", "Here it is!", "Format changed to audio. Search for anything!",
                    "Format changed to video. Search for anything!", "Unavailable. Try another one.", "Choose video:"}};

    public void send(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Database.insertQuery(update);
            CHAT_ID = update.getMessage().getChatId();
            String mess = update.getMessage().getText();
            if (mess.equals("/start")) {
                InlineKeyboardMarkup languageKeyboard = new InlineKeyboardMarkup();

                List<List<InlineKeyboardButton>> languageButtons = new ArrayList<>();
                Buttons.makeLanguageButtons(languageButtons);

                languageKeyboard.setKeyboard(languageButtons);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(CHAT_ID);
                sendMessage.setReplyMarkup(languageKeyboard);
                sendMessage.setText("Добро пожаловать! Выберите язык.\nWelcome! Choose language.");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (mess.equals("/audio")) {
                format = "m4a";
                addition = " audio";
                send(phrases[language][5]);
                return;
            }
            if (mess.equals("/video")) {
                format = "mp4";
                addition="";
                send(phrases[language][6]);
                return;
            }
            String text = mess + addition;
            String Query = URLEncoder.encode(text, StandardCharsets.UTF_8);
            try {
                YouTube.search(Query, RESULTS_AMOUNT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InlineKeyboardMarkup contentKeyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> contentButtons = new ArrayList<>();
            for (int i = 0; i < RESULTS_AMOUNT; i++) {
                String song = YouTube.contentTitle[i];
                Buttons.makeContentButtons(contentButtons, song, i + 1);
            }

            contentKeyboard.setKeyboard(contentButtons);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(CHAT_ID);
            sendMessage.setReplyMarkup(contentKeyboard);
            if (format.equals("m4a")) sendMessage.setText(phrases[language][1]); else
                sendMessage.setText(phrases[language][8]);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().length() > 1) {
                if (update.getCallbackQuery().getData().compareTo("rus") == 0)
                    language = 0; else language = 1;
                send(phrases[language][0]);
                return;
            }

            send(phrases[language][2]);

            int buttonNumber = Integer.parseInt(update.getCallbackQuery().getData()) - 1;
            String fileName = YouTube.contentTitle[buttonNumber].replaceAll("[^\\da-zA-Zа-яёА-ЯЁ]", "");
            String songUrl = YouTube.contentUrl[buttonNumber];

            Database.insertButton(update, buttonNumber + 1, YouTube.contentTitle[buttonNumber], CHAT_ID);

            String audioPath = "/content/home/resources/Audio/downloads/" + fileName + "." + format;
            File file = new File(audioPath);
            if (!file.exists()) {
                try {
                    Console.call(fileName, songUrl, format);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!file.exists()) {
                send(phrases[language][7]);
                return;
            }

            send(phrases[language][3]);

            InputFile inputFile = new InputFile(file, audioPath);

            if (format.equals("m4a")) {
                SendAudio audio = new SendAudio();
                audio.setChatId(CHAT_ID);
                audio.setAudio(inputFile);
                audio.setTitle(YouTube.contentTitle[buttonNumber]);
                audio.setCaption(phrases[language][4]);
                try {
                    this.execute(audio);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (format.equals("mp4")) {
                SendVideo video = new SendVideo();
                video.setChatId(CHAT_ID);
                video.setVideo(inputFile);
                video.setHeight(480);
                video.setWidth(854);
                video.setCaption(phrases[language][4]);
                try {
                    this.execute(video);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBot());
    }
}
