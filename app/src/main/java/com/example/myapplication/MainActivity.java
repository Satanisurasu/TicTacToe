package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private boolean player1Turn = true;
    private int player1Score = 0;
    private int player2Score = 0;
    private Button[][] buttons = new Button[3][3]; // 3x3 сетка для крестиков-ноликов
    private boolean isVsBot = false; // Переменная для проверки, играем ли мы с ботом

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Кнопки выбора режима игры
        findViewById(R.id.btnHumanVsHuman).setOnClickListener(v -> startGame(false));
        findViewById(R.id.btnHumanVsBot).setOnClickListener(v -> startGame(true));
    }

    private void startGame(boolean vsBot) {
        isVsBot = vsBot;

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this::onButtonClick);
            }
        }

        findViewById(R.id.resetButton).setOnClickListener(v -> resetGame());
        resetBoard();
    }

    private void onButtonClick(View v) {
        Button clickedButton = (Button) v;

        // Если клетка уже занята, ничего не делаем
        if (!clickedButton.getText().toString().equals("")) {
            return;
        }

        // Устанавливаем крестик или нолик
        clickedButton.setText(player1Turn ? "X" : "O");

        // Проверяем победителя
        if (checkWinner()) {
            if (player1Turn) {
                player1Score++;
                updateScores();
            } else {
                player2Score++;
                updateScores();
            }
            resetBoard(); // Сбрасываем доску после победы
        } else {
            player1Turn = !player1Turn; // Меняем игрока
            if (isVsBot && !player1Turn) {
                botPlay(); // Ход бота
            }
        }
    }

    private void botPlay() {
        Random random = new Random();
        int row, col;

        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!buttons[row][col].getText().toString().equals("")); // Ищем пустую клетку

        buttons[row][col].setText("O"); // Бот ставит "O"

        // Проверяем победителя
        if (checkWinner()) {
            player2Score++;
            updateScores();
            resetBoard(); // Сбрасываем доску после победы
        } else {
            player1Turn = true; // Возвращаем ход первому игроку
        }
    }

    // Проверяем выигрышные комбинации
    private boolean checkWinner() {
        // Проверка по строкам
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().toString().equals(buttons[i][1].getText().toString()) &&
                    buttons[i][0].getText().toString().equals(buttons[i][2].getText().toString()) &&
                    !buttons[i][0].getText().toString().equals("")) {
                return true;
            }
        }

        // Проверка по столбцам
        for (int i = 0; i < 3; i++) {
            if (buttons[0][i].getText().toString().equals(buttons[1][i].getText().toString()) &&
                    buttons[0][i].getText().toString().equals(buttons[2][i].getText().toString()) &&
                    !buttons[0][i].getText().toString().equals("")) {
                return true;
            }
        }

        // Проверка по диагоналям
        if (buttons[0][0].getText().toString().equals(buttons[1][1].getText().toString()) &&
                buttons[0][0].getText().toString().equals(buttons[2][2].getText().toString()) &&
                !buttons[0][0].getText().toString().equals("")) {
            return true;
        }

        if (buttons[0][2].getText().toString().equals(buttons[1][1].getText().toString()) &&
                buttons[0][2].getText().toString().equals(buttons[2][0].getText().toString()) &&
                !buttons[0][2].getText().toString().equals("")) {
            return true;
        }

        return false;
    }

    // Сбрасываем доску, но не счет
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        player1Turn = true; // Всегда начинает первый игрок
    }

    // Сбрасываем всю игру
    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        updateScores();
        resetBoard();
    }

    // Обновляем счет игроков
    private void updateScores() {
        TextView player1ScoreView = findViewById(R.id.player1Score);
        TextView player2ScoreView = findViewById(R.id.player2Score);
        player1ScoreView.setText("Игрок 1: " + player1Score);
        player2ScoreView.setText("Игрок 2: " + player2Score);
    }
}
