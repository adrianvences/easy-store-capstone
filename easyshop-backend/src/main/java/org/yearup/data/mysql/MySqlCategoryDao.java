package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        // get all categories
        String query = """
                SELECT * FROM categories;
                """;
        try(Connection connection = getConnection()){
            PreparedStatement getAllCategories = connection.prepareStatement(query);
            ResultSet rs = getAllCategories.executeQuery();

            while(rs.next()){
                int id = rs.getInt("category_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                categories.add(new Category(id,name,description));

            }
        } catch (SQLException e){
            System.out.println("no categories found");
            e.printStackTrace(); throw new RuntimeException();
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        String query = """
                SELECT * FROM categories WHERE category_id = ?;
                """;
        try(Connection connection = getConnection()){
            PreparedStatement getCategoryById = connection.prepareStatement(query);
            getCategoryById.setInt(1,categoryId);
            ResultSet rs = getCategoryById.executeQuery();
            while(rs.next()){
                int id = rs.getInt("category_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                return new Category(id,name,description);
            }
        }catch (SQLException e ){
            e.printStackTrace();throw new RuntimeException();
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {   String query = """
            INSERT INTO categories(name,description)
            VALUES(?,?);
            """ ;

        try(Connection connection = getConnection()){
            PreparedStatement createCategory = connection.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            createCategory.setString(1,category.getName());
            createCategory.setString(2,category.getDescription());
            createCategory.executeUpdate();

            ResultSet rs = createCategory.getGeneratedKeys();

            if(rs.next()) {
                int rsRows = rs.getInt(1);
                return new Category(rsRows, category.getName(),category.getDescription());
            }
        } catch (SQLException e){
            e.printStackTrace(); throw new RuntimeException();
        }

        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
