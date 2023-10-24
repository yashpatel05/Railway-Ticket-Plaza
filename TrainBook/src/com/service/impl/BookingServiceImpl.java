package com.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.beans.HistoryBean;
import com.beans.TrainException;
import com.constant.ResponseCode;
import com.service.BookingService;
import com.utility.DBUtil;

// Service Implementation class for handling booking details of the ticket
public class BookingServiceImpl implements BookingService {

	// Retrieves all booking transactions by customer ID
	@Override
	public List<HistoryBean> getAllBookingsByCustomerId(String customerEmailId) throws TrainException {
		List<HistoryBean> transactions = null;
		String query = "SELECT * FROM HISTORY WHERE MAILID=?";
		try {
			// Establishing database connection
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			// Setting parameters for the prepared statement
			ps.setString(1, customerEmailId);
			ResultSet rs = ps.executeQuery();
			transactions = new ArrayList<HistoryBean>();
			// Processing the result set
			while (rs.next()) {
				HistoryBean transaction = new HistoryBean();
				// Setting transaction details from the result set
				transaction.setTransId(rs.getString("transid"));
				transaction.setFrom_stn(rs.getString("from_stn"));
				transaction.setTo_stn(rs.getString("to_stn"));
				transaction.setDate(rs.getString("date"));
				transaction.setMailId(rs.getString("mailid"));
				transaction.setSeats(rs.getInt("seats"));
				transaction.setAmount(rs.getDouble("amount"));
				transaction.setTr_no(rs.getString("tr_no"));
				transactions.add(transaction);
			}
			// Closing the prepared statement
			ps.close();
		} catch (SQLException e) {
			// Handling any potential SQL exceptions
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		// Returning the list of transactions
		return transactions;
	}

	// Creates a new booking history entry
	@Override
	public HistoryBean createHistory(HistoryBean details) throws TrainException {
		HistoryBean history = null;
		String query = "INSERT INTO HISTORY VALUES(?,?,?,?,?,?,?,?)";
		try {
			// Establishing database connection
			Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			// Generating a unique transaction ID
			String transactionId = UUID.randomUUID().toString();
			// Setting parameters for the prepared statement
			ps.setString(1, transactionId);
			ps.setString(2, details.getMailId());
			ps.setString(3, details.getTr_no());
			ps.setString(4, details.getDate());
			ps.setString(5, details.getFrom_stn());
			ps.setString(6, details.getTo_stn());
			ps.setLong(7, details.getSeats());
			ps.setDouble(8, details.getAmount());
			// Executing the update and checking the response
			int response = ps.executeUpdate();
			if (response > 0) {
				// Creating a new HistoryBean instance if the update was successful
				history = (HistoryBean) details;
				history.setTransId(transactionId);
			} else {
				// Throwing an exception if the update was not successful
				throw new TrainException(ResponseCode.INTERNAL_SERVER_ERROR);
			}
			// Closing the prepared statement
			ps.close();
		} catch (SQLException e) {
			// Handling any potential SQL exceptions
			System.out.println(e.getMessage());
			throw new TrainException(e.getMessage());
		}
		// Returning the created history entry
		return history;
	}
}