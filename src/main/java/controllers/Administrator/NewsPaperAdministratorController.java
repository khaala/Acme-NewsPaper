package controllers.Administrator;


import controllers.AbstractController;
import domain.Administrator;
import domain.Article;
import domain.NewsPaper;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import services.AdministratorService;
import services.NewsPaperService;
import services.UserService;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Controller
@RequestMapping("/newsPaper/administrator")
public class NewsPaperAdministratorController extends AbstractController {
    // Services --------------------------------------------

    @Autowired
    private NewsPaperService newsPaperService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private UserService userService;



    // Constructor --------------------------------------------

    public NewsPaperAdministratorController() {
        super();
    }

    // Listing -------------------------------------------------------

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        ModelAndView result;
        User user;
        Collection<NewsPaper> newsPapersTaboo = newsPaperService.findNewsPaperByTabooIsTrue();
        Collection<NewsPaper> allNewsPapers = newsPaperService.findPublishedNewsPaper();
        SimpleDateFormat formatterEs;
        SimpleDateFormat formatterEn;
        String momentEs;
        String momentEn;

        formatterEs = new SimpleDateFormat("dd/MM/yyyy");
        momentEs = formatterEs.format(new Date());
        formatterEn = new SimpleDateFormat("yyyy/MM/dd");
        momentEn = formatterEn.format(new Date());

        result = new ModelAndView("newsPaper/list");
        result.addObject("newsPapers", newsPapersTaboo);
        result.addObject("allNewsPapers", allNewsPapers);
        result.addObject("requestUri","newsPaper/administrator/list.do");
        result.addObject("momentEs", momentEs);
        result.addObject("momentEn", momentEn);

        return result;

    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView edit(@RequestParam int newsPaperId){
        ModelAndView result;
        NewsPaper newsPaper= newsPaperService.findOne(newsPaperId);
        try{

            newsPaperService.delete(newsPaper);
            result = new ModelAndView("redirect:list.do");
        }catch (Throwable oops){
            result = createEditModelAndView(newsPaper,"article.commit.error");
        }

        return result;
    }

    // Ancillary methods ------------------------------------------------------

    protected ModelAndView createEditModelAndView(final NewsPaper newsPaper) {
        ModelAndView result;

        result = this.createEditModelAndView(newsPaper, null);
        return result;
    }

    protected ModelAndView createEditModelAndView(final NewsPaper newsPaper, final String messageCode) {
        ModelAndView result;
        User user = userService.findByPrincipal();

        Collection<NewsPaper> newsPapers = this.newsPaperService.findAllNewsPaperByUserAndNotPublished(user.getId());

        result = new ModelAndView("newsPaper/edit");
        result.addObject("newsPaper", newsPaper);
        result.addObject("newsPapers", newsPapers);
        result.addObject("actionUri","newsPaper/administrator/edit.do");
        result.addObject("message", messageCode);

        return result;
    }
}
