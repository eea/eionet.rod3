package eionet.rod.controller;

import eionet.rod.dao.AnalysisDao;
import eionet.rod.model.AnalysisDTO;
import eionet.rod.service.AnalysisService;
import eionet.rod.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    AnalysisService analysisService;

    @RequestMapping({"", "/"})
    public String analysisHome(Model model) {

        AnalysisDTO analysis = analysisService.getStatistics();
        model.addAttribute("analysis", analysis);
        BreadCrumbs.set(model, "Database Content Statistics");
        model.addAttribute("activeTab", "analysis");
        model.addAttribute("title", "Database Content Statistics");
        return "analysis";
    }

}
