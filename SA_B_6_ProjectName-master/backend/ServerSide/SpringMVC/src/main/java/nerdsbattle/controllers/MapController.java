package nerdsbattle.controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nerdsbattle.models.MapModel;

/**
 * Controller to handle any requests
 * related to maps. Specifically to
 * retrieve data about maps from the
 * database.
 * @author Sammy
 *
 */
@Controller
public class MapController {
	
  /**
   * Handle request to /GetMaps.
   * Takes parameters of column names
   * with optional values to represent 
   * where conditions.
   * @param req
   * The HttpServletRequest that sent the GetMaps request.
   * @return
   * All the Maps that meet the criteria
   */
	@RequestMapping("/GetMaps")
	@ResponseBody
	public String handleGetAllMaps(HttpServletRequest req, HttpServletResponse res) {
		MapModel mHandler = new MapModel();
		HashMap<String, String> queryData = mHandler.processSelectRequest(req);
		String result = mHandler.getColumnsFromEntryWhere(queryData);
		return result;
	}
	
	/**
	 * Handle request to /UpdateMap.
	 * Takes parameters of column names with
	 * values to update the specified column
	 * in the database.
	 * @param req
	 * The HttpServletRequest that sent the UpdateMap request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/UpdateMap")
	public void handleUpdateMap(HttpServletRequest req, HttpServletResponse res) {
		MapModel mHandler = new MapModel();
		HashMap<String, String> newData = mHandler.processUpdateRequest(req);
		mHandler.updateEntry(newData);	
		System.out.println("In update");
		try {
			res.getOutputStream().print("Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle request to /AddMap.
	 * Takes parameters of column name and values
	 * to be inserted into the Map table in the
	 * database.
	 * @param req
	 * The HttpServletRequest that sent the AddMap request.
	 * @return
	 * "Success" if successfully added. An error message
	 * otherwise.
	 */
	@RequestMapping("/AddMap")
	@ResponseBody
	public String handleAddMap(HttpServletRequest req, HttpServletResponse res) {
		MapModel mModel = new MapModel();
		mModel.connect();
		HashMap<String, String> newMap = mModel.processUpdateRequest(req);
		if(mModel.isValidEntry(newMap)) {
		  if(mModel.mapExists(newMap.get("Title"), false)) {
		    mModel.disconnect();
		    return "Error, Map already exists.";
		  }
		  //Map doesnt exits and is a valid entry. Add it to db and return success.
		  mModel.insertEntry(newMap, false);
		  mModel.disconnect();
			return "Success";
		}
		mModel.disconnect();
		return "Error, invalid Map. Must provide values for 'Title', 'Creator', and 'BackgroundID' parameters of this request. 'Obstacles' is optional.";
	}
	
	/**
	 * Handle request to /DeleteMap.
	 * Takes parameter Title and deletes
	 * that map from the Map table in the
	 * database.
	 * @param req
	 * The HttpServletRequest that sent the DeleteMap request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/DeleteMap")
	public void handleDeleteMap(HttpServletRequest req, HttpServletResponse res) {
		MapModel mHandler = new MapModel();
		String title = req.getParameter("Title");
		mHandler.deleteEntry(title, true);
		
		try {
			res.getOutputStream().print("Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle request to /GetMap.
	 * Takes parameter Title and gets the map
	 * with its obstacles from the map and
	 * obstacle tables in the database.
	 * @param req
	 * The HttpServletRequest that sent the GetMap request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/GetMap")
	public void handleGetMap(HttpServletRequest req, HttpServletResponse res) {
		MapModel mHandler = new MapModel();
		String maptitle = req.getParameter("Title");
		String result = mHandler.getMapWithObstacles(maptitle);
		try {
			res.getOutputStream().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
