package eionet.rod.controller;

import eionet.rod.model.UndoDTO;
import eionet.rod.service.UndoService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/updatehistory")
public class UpdateHistoryController {

    @Autowired
    UndoService undoService;

    @RequestMapping({"", "/"})
    public String updateHistoryHome(Model model, @RequestParam(required = false) Integer id, @RequestParam(required = false) String object, @RequestParam(required = false) String username) {

        List<UndoDTO> history;
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (object != null) {
            params.addValue("extraSQL", id);
            if ("S".equals(object)) {
                history = undoService.getUpdateHistory("AND U1.VALUE = :extraSQL AND U1.TAB = 'T_SOURCE' ", params);
            } else {
                history = undoService.getUpdateHistory("AND U1.VALUE = :extraSQL AND U1.TAB = 'T_OBLIGATION' ", params);
            }
        } else if (username != null) {
            params.addValue("extraSQL", username);
            history = undoService.getUpdateHistory("AND U2.VALUE = :extraSQL ", params);
        } else {
            history = undoService.getUpdateHistory("", params);
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
        model.addAttribute("title", "Most recent ROD updates");

        return "updatehistory";

    }

}
