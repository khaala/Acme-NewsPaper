package controllers.User;

import controllers.AbstractController;
import domain.Article;
import domain.FollowUp;
import domain.NewsPaper;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import services.ArticleService;
import services.FollowUpService;
import services.NewsPaperService;
import services.UserService;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Controller
@RequestMapping("/followUp/user")
public class FollowUpUserController extends AbstractController {
    // Services --------------------------------------------

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private NewsPaperService newsPaperService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    // Constructor --------------------------------------------

    public FollowUpUserController() {
        super();
    }

    // Creation ------------------------------------------------------

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView create() {
        ModelAndView result;
        FollowUp followUp;

        followUp = followUpService.create();
        result = createEditModelAndView(followUp);

        return result;
    }

    // Listing -------------------------------------------------------

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        ModelAndView result;
        User user;
        Collection<FollowUp> followUps;

        SimpleDateFormat formatterEs;
        SimpleDateFormat formatterEn;
        String momentEs;
        String momentEn;

        formatterEs = new SimpleDateFormat("dd/MM/yyyy");
        momentEs = formatterEs.format(new Date());
        formatterEn = new SimpleDateFormat("yyyy/MM/dd");
        momentEn = formatterEn.format(new Date());

        user = userService.findByPrincipal();
        followUps = followUpService.findFollowupsByUserId(user.getId());

        result = new ModelAndView("followUp/list");
        result.addObject("followUps", followUps);
        result.addObject("user",user);
        result.addObject("requestUri","followUp/user/list.do");

        result.addObject("momentEs", momentEs);
        result.addObject("momentEn", momentEn);

        return result;

    }

    //  Edition ----------------------------------------------------------------
    @RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
    public ModelAndView save(@Valid FollowUp followUp, BindingResult binding) {
        ModelAndView result;
        if (binding.hasErrors())
            result = this.createEditModelAndView(followUp);
        else
            try {
                followUpService.save(followUp);
                result = new ModelAndView("redirect:list.do");
            } catch (final Throwable oops) {
                result = this.createEditModelAndView(followUp, "article.commit.error");
            }
        return result;
    }

    // Display ----------------------------------------------------------------

    @RequestMapping(value = "/display", method = RequestMethod.GET)
    public ModelAndView display(@RequestParam  int followUpId) {
        ModelAndView result;
        FollowUp followUp;
        SimpleDateFormat formatterEs;
        SimpleDateFormat formatterEn;
        String momentEs;
        String momentEn;

        followUp = followUpService.findOne(followUpId);
        formatterEs = new SimpleDateFormat("dd/MM/yyyy");
        momentEs = formatterEs.format(followUp.getMoment());
        formatterEn = new SimpleDateFormat("yyyy/MM/dd");
        momentEn = formatterEn.format(followUp.getMoment());

        result = new ModelAndView("followUp/display");
        result.addObject("followUp", followUp);
        result.addObject("cancelURI", "followUp/user/list.do");

        result.addObject("momentEs", momentEs);
        result.addObject("momentEn", momentEn);


        return result;
    }


    // Ancillary methods ------------------------------------------------------

    protected ModelAndView createEditModelAndView(FollowUp followUp) {
        ModelAndView result;

        result = this.createEditModelAndView(followUp, null);
        return result;
    }

    protected ModelAndView createEditModelAndView(FollowUp followUp, final String messageCode) {
        ModelAndView result;
        User principal;
        Collection<Article> articles;

        principal = userService.findByPrincipal();
        articles = articleService.findPublishArticlesByUserId(principal.getId());

        result = new ModelAndView("followUp/edit");

        result.addObject("followUp", followUp);
        result.addObject("articles", articles);
        result.addObject("message", messageCode);
        result.addObject("actionUri","followUp/user/edit.do");

        return result;
    }
}