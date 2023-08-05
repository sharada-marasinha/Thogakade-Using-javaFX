package controller.item;

import db.DBConnection;
import dto.Item;
import dto.OrderDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;

public class ItemController implements ItemService {
    public static boolean updateStock(ArrayList<OrderDetails> orderDetails) throws SQLException, ClassNotFoundException {
        for (OrderDetails orderDetail : orderDetails) {
            if(!updateStock(orderDetail)){
                return false;
            }
        }
        return true;
    }
    public static boolean updateStock(OrderDetails orderDetail) throws ClassNotFoundException, SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("Update Item set qtyOnHand=qtyOnHand-? where code=?");
        stm.setObject(1,orderDetail.getQty());
        stm.setObject(2,orderDetail.getItemCode());
        return stm.executeUpdate()>0;
    }

    @Override
    public boolean addItem(Item item) {
        String SQL = "Insert into Item Values(?,?,?,?)";
        Connection connection = null;
        int i = -1;
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            psTm.setObject(1, item.getCode());
            psTm.setObject(2, item.getDescription());
            psTm.setObject(3, item.getUnitPrice());
            psTm.setObject(4, item.getQtyOnHand());
            i = psTm.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        return i > 0 ? true : false;
    }

    @Override
    public boolean updateItem(Item item) {
        String sql = "UPDATE Item SET description = ?, unitPrice = ?, qtyOnHand = ? WHERE code = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(sql);
            psTm.setString(1, item.getDescription());
            psTm.setDouble(2, item.getUnitPrice());
            psTm.setInt(3, item.getQtyOnHand());
            psTm.setString(4, item.getCode());
            int i = psTm.executeUpdate();
            if (i>0){
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        return false;
    }

    @Override
    public Item searchItem(String itemCode) {
        String SQL = "Select * From Item where code='" + itemCode + "'";
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SQL);
            if (resultSet.next()) {
                Item item = new Item(itemCode, resultSet.getString(2), resultSet.getDouble(3), resultSet.getInt(4));
                return item;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean deleteItem(String itemCode) {
        int i = 0;
        try {
            i = DBConnection.getInstance().getConnection().createStatement().executeUpdate("Delete From Item where code='" + itemCode + "'");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        return i > 0 ? true : false;
    }

    @Override
    public ObservableList<Item> getAllItem() {
        String SQL = "Select * From item";
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery(SQL);

            while (rst.next()) {
                Item item = new Item(rst.getString(1), rst.getString(2), rst.getDouble(3), rst.getInt(4));
                list.add(item);
            }
           return list;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
