package com.java.ticTacToe.service;


import static com.java.ticTacToe.model.GameStatus.NEW;
import static com.java.ticTacToe.model.GameStatus.IN_PROGRESS;
import static com.java.ticTacToe.model.GameStatus.FINISHED;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.java.ticTacToe.exception.InvalidGameException;
import com.java.ticTacToe.exception.InvalidParamException;
import com.java.ticTacToe.exception.NotFoundException;
import com.java.ticTacToe.model.Game;
import com.java.ticTacToe.model.GamePlay;
import com.java.ticTacToe.model.Player;
import com.java.ticTacToe.model.TicToe;
import com.java.ticTacToe.storage.GameStorage;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameService {
	
	public Game createGame(Player player) {
		
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameId(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(NEW);
		GameStorage.getInstance().setGames(game);
		return game;
	}
	
	public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException {
		
		if(!GameStorage.getInstance().getGames().containsKey(gameId)) {
			throw new InvalidParamException("Game with the provided Id doesn't exist.");
		}
		
		Game game = GameStorage.getInstance().getGames().get(gameId);
		
		if(game.getPlayer2() != null) {
			throw new InvalidGameException("Game is not valid anymore.");
		}
		
		game.setPlayer2(player2);
		game.setStatus(IN_PROGRESS);
		GameStorage.getInstance().setGames(game);
		return game;
	}

	public Game connectToRandomGame(Player player2) throws NotFoundException {
		
		Game game = GameStorage.getInstance().getGames().values().stream()
			.filter(it->it.getStatus().equals(NEW))
			.findFirst().orElseThrow(()->new NotFoundException("Game Not Found."));
		
		game.setPlayer2(player2);
		game.setStatus(IN_PROGRESS);
		GameStorage.getInstance().setGames(game);
		return game;
	}
	
	public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
		if(!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
			throw new NotFoundException("Game not found.");
		}
		
		Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
		if(game.getStatus().equals(FINISHED)) {
			throw new InvalidGameException("Game is already finished.");
		}
		
		int [][] board = game.getBoard();
		board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();
		
		Boolean xWinner = checkWinner(game.getBoard(), TicToe.X);
		Boolean oWinner = checkWinner(game.getBoard(), TicToe.O);
		
		if(xWinner) {
			game.setWinner(TicToe.X);
		}else if(oWinner) {
			game.setWinner(TicToe.O);
		}
		
		GameStorage.getInstance().setGames(game);
		
		return game;
	}

	private Boolean checkWinner(int[][] board, TicToe ticToe) {
		int [] boardArray = new int[9];
		int counterIndex = 0;
		//We put our 2 dimensional board[][] array in one dimensional array  boardArray[]
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				boardArray[counterIndex] = board[i][j];
				counterIndex++;
			}
		}
		// Now making win combinations
		int[][] winCombinations = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6} };
		for(int i = 0; i < winCombinations.length; i++) {
			int counter = 0;
			for(int j = 0; j < winCombinations[i].length; j++) {
				if(boardArray[winCombinations[i][j]] == ticToe.getValue()) {
					counter++;
					if(counter == 3) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
