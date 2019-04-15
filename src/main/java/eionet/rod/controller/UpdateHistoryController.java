package eionet.rod.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.dao.UndoService;
import eionet.rod.model.UndoDTO;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;

@Controller
@RequestMapping("/updatehistory")
public class UpdateHistoryController {

	@Autowired
    UndoService undoService;
	
	@RequestMapping({"", "/"}) 
	public String updateHistoryHome(Model model, @RequestParam(required = false) Integer id, @RequestParam(required = false) String object, @RequestParam(required = false) String username) {
		
		List<UndoDTO> history;
		
		if (object != null) {
			if ("S".equals(object)) {
				history = undoService.getUpdateHistory("AND U1.VALUE = " + id + " AND U1.TAB = 'T_SOURCE' ");
			} else {
				history = undoService.getUpdateHistory("AND U1.VALUE = " + id + " AND U1.TAB = 'T_OBLIGATION' ");
			}			
		} else if (username != null) {
			history = undoService.getUpdateHistory("AND U2.VALUE = '" + username + "' ");
		} else {
			history = undoService.getUpdateHistory("");
		}
		
		if (history != null) {
			for (UndoDTO undoDTO : history) {
				undoDTO.setDate(RODUtil.miliseconds2Date(undoDTO.getUndoTime()));
				undoDTO.setDescription(undoDTO.truncateText(undoDTO.getDescription()));
			}
		}
		model.addAttribute("history", history);
		
		BreadCrumbs.set(model, "Most recent ROD updates");
		model.addAttribute("activeTab", "analysis");
		model.addAttribute("title","Most recent ROD updates");
		
		return "updatehistory";
		
	}
	
}
