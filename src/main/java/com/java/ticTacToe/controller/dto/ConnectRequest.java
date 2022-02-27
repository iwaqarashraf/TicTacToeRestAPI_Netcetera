package com.java.ticTacToe.controller.dto;

import com.java.ticTacToe.model.Player;

import lombok.Data;

@Data
public class ConnectRequest {
	
	private Player player;
	private String gameId;
		
}
