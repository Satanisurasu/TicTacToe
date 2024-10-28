package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean player1Turn = true;
    private int player1Score = 0;
    private int player2Score = 0;
    private Button[][] buttons = new Button[3][3];
    private boolean isVsBot = false;
    private final Handler handler = new Handler(); // для задержки хода бота

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                buttons[i][j].setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }

        findViewById(R.id.resetButton).setOnClickListener(v -> resetGame());
        resetBoard();
    }

    private void onButtonClick(View v) {
        Button clickedButton = (Button) v;

        if (!clickedButton.getText().toString().equals("")) return;

        clickedButton.setText(player1Turn ? "X" : "O");

        if (checkWinner()) {
            if (player1Turn) {
                player1Score++;
                updateScores();
                highlightWinningButtons("X");
                handler.postDelayed(this::resetBoard, 1500); // задержка перед сбросом после выигрыша
            } else {
                player2Score++;
                updateScores();
                highlightWinningButtons("O");
                handler.postDelayed(this::resetBoard, 1500);
            }
        } else {
            player1Turn = !player1Turn;
            if (isVsBot && !player1Turn) {
                handler.postDelayed(this::botPlay, 2500); // Задержка 500 мс перед ходом бота
            }
        }
    }

    private void botPlay() {
        if (tryToWinOrBlock("O") || tryToWinOrBlock("X")) {
            // Ход уже сделан (либо выиграл, либо блокировал)
        } else {
            makeOptimalMove(); // Делает центральный ход или случайный, если ничего лучше нет
        }

        if (checkWinner()) {
            player2Score++;
            updateScores();
            highlightWinningButtons("O");
            handler.postDelayed(this::resetBoard, 1500); // задержка перед сбросом после выигрыша
        } else {
            player1Turn = true;
        }
    }

    private boolean tryToWinOrBlock(String symbol) {
        for (int i = 0; i < 3; i++) {
            // Проверка строк
            if (checkTwoInLine(i, 0, i, 1, i, 2, symbol)) return true;
            // Проверка столбцов
            if (checkTwoInLine(0, i, 1, i, 2, i, symbol)) return true;
        }
        // Проверка диагоналей
        return checkTwoInLine(0, 0, 1, 1, 2, 2, symbol) ||
                checkTwoInLine(0, 2, 1, 1, 2, 0, symbol);
    }

    private boolean checkTwoInLine(int x1, int y1, int x2, int y2, int x3, int y3, String symbol) {
        if (buttons[x1][y1].getText().toString().equals(symbol) &&
                buttons[x2][y2].getText().toString().equals(symbol) &&
                buttons[x3][y3].getText().toString().equals("")) {
            buttons[x3][y3].setText("O");
            return true;
        }
        if (buttons[x1][y1].getText().toString().equals(symbol) &&
                buttons[x3][y3].getText().toString().equals(symbol) &&
                buttons[x2][y2].getText().toString().equals("")) {
            buttons[x2][y2].setText("O");
            return true;
        }
        if (buttons[x2][y2].getText().toString().equals(symbol) &&
                buttons[x3][y3].getText().toString().equals(symbol) &&
                buttons[x1][y1].getText().toString().equals("")) {
            buttons[x1][y1].setText("O");
            return true;
        }
        return false;
    }

    private void makeOptimalMove() {
        if (buttons[1][1].getText().toString().equals("")) {
            buttons[1][1].setText("O");
            return;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    buttons[i][j].setText("O");
                    return;
                }
            }
        }
    }

    private void highlightWinningButtons(String playerSymbol) {
        int color = getResources().getColor(R.color.win_color);
        for (int i = 0; i < 3; i++) {
            if (checkAndHighlightRow(i, playerSymbol, color)) return;
            if (checkAndHighlightColumn(i, playerSymbol, color)) return;
        }
        checkAndHighlightDiagonals(playerSymbol, color);
    }

    private boolean checkAndHighlightRow(int row, String symbol, int color) {
        if (buttons[row][0].getText().toString().equals(symbol) &&
                buttons[row][1].getText().toString().equals(symbol) &&
                buttons[row][2].getText().toString().equals(symbol)) {
            buttons[row][0].setBackgroundColor(color);
            buttons[row][1].setBackgroundColor(color);
            buttons[row][2].setBackgroundColor(color);
            return true;
        }
        return false;
    }

    private boolean checkAndHighlightColumn(int col, String symbol, int color) {
        if (buttons[0][col].getText().toString().equals(symbol) &&
                buttons[1][col].getText().toString().equals(symbol) &&
                buttons[2][col].getText().toString().equals(symbol)) {
            buttons[0][col].setBackgroundColor(color);
            buttons[1][col].setBackgroundColor(color);
            buttons[2][col].setBackgroundColor(color);
            return true;
        }
        return false;
    }

    private void checkAndHighlightDiagonals(String symbol, int color) {
        if (buttons[0][0].getText().toString().equals(symbol) &&
                buttons[1][1].getText().toString().equals(symbol) &&
                buttons[2][2].getText().toString().equals(symbol)) {
            buttons[0][0].setBackgroundColor(color);
            buttons[1][1].setBackgroundColor(color);
            buttons[2][2].setBackgroundColor(color);
        } else if (buttons[0][2].getText().toString().equals(symbol) &&
                buttons[1][1].getText().toString().equals(symbol) &&
                buttons[2][0].getText().toString().equals(symbol)) {
            buttons[0][2].setBackgroundColor(color);
            buttons[1][1].setBackgroundColor(color);
            buttons[2][0].setBackgroundColor(color);
        }
    }
    private boolean checkWinner() {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().toString().equals(buttons[i][1].getText().toString()) &&
                    buttons[i][0].getText().toString().equals(buttons[i][2].getText().toString()) &&
                    !buttons[i][0].getText().toString().equals("")) {
                return true;
            }
        }

        // Проверка столбцов
        for (int i = 0; i < 3; i++) {
            if (buttons[0][i].getText().toString().equals(buttons[1][i].getText().toString()) &&
                    buttons[0][i].getText().toString().equals(buttons[2][i].getText().toString()) &&
                    !buttons[0][i].getText().toString().equals("")) {
                return true;
            }
        }

        // Проверка диагоналей
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



    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }
        player1Turn = true;
    }

    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        updateScores();
        resetBoard();
    }

    private void updateScores() {
        TextView player1ScoreView = findViewById(R.id.player1Score);
        TextView player2ScoreView = findViewById(R.id.player2Score);
        player1ScoreView.setText("Игрок 1: " + player1Score);
        player2ScoreView.setText("Игрок 2: " + player2Score);
    }
}
