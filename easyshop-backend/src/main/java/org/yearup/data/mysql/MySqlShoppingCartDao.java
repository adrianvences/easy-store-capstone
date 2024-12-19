package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) { super(dataSource); }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String query = """
                SELECT * FROM shopping_cart
                JOIN products ON products.product_id = shopping_cart.product_id
                WHERE user_id = ?;
                """;
        try(Connection connection = getConnection()) {
            PreparedStatement userById = connection.prepareStatement(query);
            userById.setInt(1,userId);
            ResultSet rs = userById.executeQuery();

            while (rs.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                Product product = new Product(
                        rs.getInt("shopping_cart.product_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("category_id"),
                        rs.getString("description"),
                        rs.getString("color"),
                        rs.getInt("stock"),
                        rs.getBoolean("featured"),
                        rs.getString("image_url"));

                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));
                cart.add(item);

            }
            return cart;

        } catch (SQLException e) {
            e.printStackTrace(); throw new RuntimeException();
        }
    }

    public ShoppingCart addToShoppingCart(int userId, Product product) {
        ShoppingCart cart = getByUserId(userId);
        String query = """
                INSERT INTO shopping_cart(user_id, product_id)
                    VALUES(?, ?);
                """;
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, product.getProductId());

            statement.executeUpdate();

            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            cart.add(item);
            return cart;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
