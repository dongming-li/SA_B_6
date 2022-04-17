package nerdsbattle.controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import nerdsbattle.models.ItemModel;

/**
 * Controller that handles any requests related
 * to items. Specifically to retrieve data about
 * items from the database.
 * @author Sammy
 *
 */
@Controller
public class ItemController {
	
	/**
	 * Handle the request to /GetItems.
	 * Takes parameter of column names and
	 * optional values to specify a where conditions.
	 * @param req
	 * The HttpServletRequest that sent the GetItems request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/GetItems")
	public void handleGetItem(HttpServletRequest req, HttpServletResponse res) {
		ItemModel iHandler = new ItemModel();
		HashMap<String, String> queryData = iHandler.processSelectRequest(req);
		String result = iHandler.getColumnsFromEntryWhere(queryData);
		
		try {
			res.getOutputStream().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handle request to /UpdateItem.
	 * Takes parameters of column names with values
	 * that are to be used to update the specified column
	 * in the database.
	 * @param req
	 * The HttpServletRequest that sent the UpdateItem request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/UpdateItem")
	public void handleUpdateItem(HttpServletRequest req, HttpServletResponse res) {
		ItemModel iHandler = new ItemModel();
		HashMap<String, String> newData = iHandler.processUpdateRequest(req);
		iHandler.updateEntry(newData);	
		
		try {
			res.getOutputStream().print("Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle request to /AddItem.
	 * Takes parameters of column names with values
	 * that are to be inserted into the associated column.
	 * @param req
	 * The HttpServletRequest that sent the AddItem request.
	 * @param res
	 * The HttpServletResponse to send the results to.
	 */
	@RequestMapping("/AddItem")
	public void handleAddItem(HttpServletRequest req, HttpServletResponse res) {
		ItemModel iHandler = new ItemModel();
		HashMap<String, String> newItem = iHandler.processUpdateRequest(req);
		if(iHandler.isValidEntry(newItem)) {
			iHandler.insertEntry(newItem, true);
		}
		
		try {
			res.getOutputStream().print("Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle request to /DeleteItem
	 * Takes parameter Title that is the 
	 * title of the item to delete from
	 * the database.
	 * @param req
	 * The HttpServletRequest that sent the DeleteItem request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/DeleteItem")
	public void handleDeleteItem(HttpServletRequest req, HttpServletResponse res) {
		ItemModel iHandler = new ItemModel();
		String title = req.getParameter("Title");
		iHandler.deleteEntry(title, true);
		
		try {
			res.getOutputStream().print("Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
