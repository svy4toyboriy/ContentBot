package contentDealer;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    public static void makeContentButtons(List<List<InlineKeyboardButton>> l, String name, int i) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton contentName = new InlineKeyboardButton();
        contentName.setText(i + ". " + name);
        contentName.setCallbackData(i + "");
        row.add(contentName);
        l.add(row);
    }
    public static void makeLanguageButtons(List<List<InlineKeyboardButton>> languageButtons) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton language1 = new InlineKeyboardButton();
        language1.setText("\uD83C\uDDF7\uD83C\uDDFA Русский");
        language1.setCallbackData("rus");
        row.add(language1);
        InlineKeyboardButton language2 = new InlineKeyboardButton();
        language2.setText("\uD83C\uDDEC\uD83C\uDDE7 English");
        language2.setCallbackData("eng");
        row.add(language2);

        languageButtons.add(row);
    }
}
