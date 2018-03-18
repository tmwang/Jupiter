package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import aglorism.GeoRecommendation;
import entity.Item;
import entity.Item.ItemBuilder;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		GeoRecommendation recommendation = new GeoRecommendation();
		List<Item> items = recommendation.recommendItems(userId, lat, lon);

		JSONArray result = new JSONArray();
		try {
			for (Item item : items) {
				result.put(item.toJSONObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, result); 


	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		doGet(request, response);
	}

/**
 * Helper methods
 */

//  {
//        "_embedded": {
//	    "venues": [
//	        {
//		        "address": {
//		           "line1": "101 First St,",
//		           "line2": "Suite 101",
//		           "line3": "...",
//		        },
//		        "city": {
//		        	"name": "San Francisco"
//		        }
//		        ...
//	        },
//	        ...
//	    ]
//        }
          //        ...
//  }
private String getAddress(JSONObject event) throws JSONException {
	if (!event.isNull("_embedded")) {
		JSONObject embedded = event.getJSONObject("_embedded");
		if (!embedded.isNull("venues")) {
			JSONArray venues = embedded.getJSONArray("venues");
			if (venues.length() > 0) {
				JSONObject venue = venues.getJSONObject(0);
				StringBuilder sb = new StringBuilder();
				
				if (!venue.isNull("address")) {
					JSONObject address = venue.getJSONObject("address");
					if (!address.isNull("line1")) {
						sb.append(address.getString("line1"));
					}
					if (!address.isNull("line2")) {
						sb.append(address.getString("line2"));
					}
					if (!address.isNull("line3")) {
						sb.append(address.getString("line3"));
					}
					sb.append(",");
				}
				if (!venue.isNull("city")) {
					JSONObject city = venue.getJSONObject("city");
					if (!city.isNull("name")) {
						sb.append(city.getString("name"));
					}
				}
				return sb.toString();
			}
		}
	}	
	return null;
}


// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
private String getImageUrl(JSONObject event) throws JSONException {
	if (!event.isNull("images")) {
		JSONArray array = event.getJSONArray("images");
		for (int i = 0; i < array.length(); i++) {
			JSONObject image = array.getJSONObject(i);
			if (!image.isNull("url")) {
				return image.getString("url");
			}
		}
	}
	return null;
}

// {"classifications" : [{"segment": {"name": "music"}}, ...]}
private Set<String> getCategories(JSONObject event) throws JSONException {
	Set<String> categories = new HashSet<>();
	if (!event.isNull("classifications")) {
		JSONArray classifications = event.getJSONArray("classifications");
		for (int i = 0; i < classifications.length(); i++) {
			JSONObject classification = classifications.getJSONObject(i);
			if (!classification.isNull("segment")) {
				JSONObject segment = classification.getJSONObject("segment");
				if (!segment.isNull("name")) {
					String name = segment.getString("name");
					categories.add(name);
				}
			}
		}
	}
	return categories;
}
}

