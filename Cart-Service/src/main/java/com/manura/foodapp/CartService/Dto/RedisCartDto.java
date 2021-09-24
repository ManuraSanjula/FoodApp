package com.manura.foodapp.CartService.Dto;

import java.util.List;

import lombok.Data;

@Data
public class RedisCartDto {
   private String user;
   private List<CartDto> cartDtos;
}
