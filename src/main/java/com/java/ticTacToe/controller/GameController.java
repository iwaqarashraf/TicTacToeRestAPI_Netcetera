package com.java.ticTacToe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.ticTacToe.controller.dto.ConnectRequest;
import com.java.ticTacToe.exception.InvalidGameException;
import com.java.ticTacToe.exception.InvalidParamException;
import com.java.ticTacToe.exception.NotFoundException;
import com.java.ticTacToe.model.Game;
import com.java.ticTacToe.model.GamePlay;
import com.java.ticTacToe.model.Player;
import com.java.ticTacToe.service.GameService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
	
	private final GameService gameService;
	private final SimpMessagingTemplate simpMessagingTemplate;
	
	
	@PostMapping("/start")
	public ResponseEntity<Game> start(@RequestBody Player player){
		return ResponseEntity.ok(gameService.createGame(player));
	}
	
	
	@PostMapping("/connect")
	public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException{
		return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
	}
	
	@PostMapping("/connect/random")
	public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws NotFoundException{
		return ResponseEntity.ok(gameService.connectToRandomGame(player));
	}
	
	@PostMapping("/gameplay")
	public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws InvalidGameException, NotFoundException{
		Game game = gameService.gamePlay(request);
		//we have used web socket here to notify the other player to move his turn
		simpMessagingTemplate.convertAndSend("/topic/game-progress"+game.getGameId() , game);
		return ResponseEntity.ok(game);
	}
}
