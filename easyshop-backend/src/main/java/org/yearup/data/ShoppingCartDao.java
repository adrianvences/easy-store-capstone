package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{


    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addToShoppingCart(int userId, Product product);
    void updateItemQuantity(int userId, ShoppingCartItem item , int productId);
}
