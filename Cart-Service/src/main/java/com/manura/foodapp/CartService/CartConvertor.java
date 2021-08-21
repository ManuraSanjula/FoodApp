package com.manura.foodapp.CartService;

import org.modelmapper.Converters.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.manura.foodapp.CartService.Table.CartTable;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;

import io.r2dbc.spi.Row;

@ReadingConverter
public class CartConvertor implements Converter<Row, CartTable> {

	@Override
	public CartTable convert(Row source) {
		UserTable userTable = UserTable.builder().publicId(source.get("public_id", String.class))
				.firstName(source.get("first_name", String.class)).lastName(source.get("last_name", String.class))
				.email(source.get("email", String.class)).active(source.get("active", Boolean.class))
				.emailVerify(source.get("email_verify", Boolean.class)).address(source.get("address", String.class))
				.pic(source.get("pic", String.class)).build();

		FoodTable foodTable = FoodTable.builder().name(source.get("name", String.class))
				.publicId(source.get("public_id", String.class)).description(source.get("description", String.class))
				.type(source.get("type", String.class)).unlikes(source.get("unlikes", Integer.class))
				.likes(source.get("likes", Integer.class)).price(source.get("price", Double.class))
				.rating(source.get("rating", Double.class)).coverImage(source.get("cover_image", String.class)).build();
		
        CartTable cartTable = CartTable.builder().publicId(source.get("public_id", String.class)).food(foodTable).user(userTable)
        		.price(source.get("price", Double.class)).count(source.get("count", Long.class))
        		.build();
		return cartTable;
	}

}


