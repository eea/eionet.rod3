package eionet.rod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eionet.rod.dao.UndoService;
import eionet.rod.model.UndoDTO;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;

@Controller
@RequestMapping("/versions")
public class VersionsController {
	
	@Autowired
    UndoService undoService;
	
	@RequestMapping({"", "/"}) 
	public String versionsHome(Model model) {
		
		List<UndoDTO> versions = undoService.getPreviousActionsGeneral();
		if (versions != null) {
			UndoDTO undo;
			for (int i = 0; i < versions.size(); i++) {
				undo = versions.get(i);
				undo.setDate(RODUtil.miliseconds2Date(undo.getUndoTime()));
			}
		}
		model.addAttribute("versions", versions);
		
		BreadCrumbs.set(model, "Previous Versions - ROD");
		model.addAttribute("activeTab", "globalHistory");
		model.addAttribute("title","Previous Versions - ROD");
		
		return "versions";		
	}

}
