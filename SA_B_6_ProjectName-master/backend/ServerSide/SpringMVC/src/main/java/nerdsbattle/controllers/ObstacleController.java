package nerdsbattle.controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import nerdsbattle.models.ObstacleModel;

/**
 * Controller to handle any requests related
 * to obstacles. Specifically to retrieve data
 * from the Obstacle table in the database.
 * @author Sammy
 *
 */
@Controller
public class ObstacleController {
	
  /**
   * Handle request to /GetObstacles.
   * Takes parameters of column names and optional
   * values to represent where conditions.
   * @param req
   * The HttpServletRequest that sent the GetObstacles request.
   * @param res
   * The HttpServletResponse to send the data to.
   */
	@RequestMapping("/GetObstacles")
	public void handleGetObstacle(HttpServletRequest req, HttpServletResponse res) {
		ObstacleModel oHandler = new ObstacleModel();
		HashMap<String, String> queryData = oHandler.processSelectRequest(req);
		String result = oHandler.getColumnsFromEntryWhere(queryData);
		
		try {
			res.getOutputStream().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handle request to /UpdateObstacle.
	 * Takes parameters of column names and 
	 * values to update the specified column in
	 * the Obstacle table in the database.
	 * @param req
	 * The HttpServletRequest that sent the UpdateObstacle request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/UpdateObstacle")
	public void handleUpdateObstacle(HttpServletRequest req, HttpServletResponse res) {
		ObstacleModel oHandler = new ObstacleModel();
		try {
			HashMap<String, String> newData = oHandler.processUpdateRequest(req);
			oHandler.updateEntry(newData);	
			res.getOutputStream().print("Success");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle request to /AddObstacle.
	 * Takes parameters of column names and values
	 * to be inserted into the Obstacle table in
	 * the database.
	 * @param req
	 * The HttpServletRequest that sent the AddObstacle request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/AddObstacle")
	public void handleAddObstacle(HttpServletRequest req, HttpServletResponse res) {
		ObstacleModel oHandler = new ObstacleModel();
		HashMap<String, String> newObstacle = oHandler.processUpdateRequest(req);
		if(oHandler.isValidEntry(newObstacle)) {
			oHandler.insertEntry(newObstacle, true);
		}
	}

	/**
	 * Handle request to /DeleteObstacle.
	 * Takes parameter of Title and deletes
	 * that Obstacle from the Obstacle table
	 * in the database.
	 * @param req
	 * The HttpServletRequest that sent the DeleteObstacle request.
	 * @param res
	 * The HttpServletResponse to send the data to.
	 */
	@RequestMapping("/DeleteObstacle")
	public void handleDeleteObstacle(HttpServletRequest req, HttpServletResponse res) {
		ObstacleModel oHandler = new ObstacleModel();
		String title = req.getParameter("Title");
		oHandler.deleteEntry(title, true);
	}
	
}
