package com.java.ticTacToe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum TicToe {
	X(1), O(2);
	
	private Integer value;
	
}
