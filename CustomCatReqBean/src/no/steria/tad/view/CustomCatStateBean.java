package no.steria.tad.view;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import oracle.iam.ui.catalog.view.backing.CatStateBean;
import oracle.iam.ui.platform.view.backing.BaseMB;
import oracle.iam.ui.platform.view.backing.OIMContext;


public class CustomCatStateBean extends BaseMB{

    public CustomCatStateBean() {
        super();
    }

    public CatStateBean getCatStateBean() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ELContext context = fctx.getELContext();
        ValueExpression createValueExpression = expressionFactory.createValueExpression(context, "#{pageFlowScope.catStateBean}", CatStateBean.class);
        return  (CatStateBean)createValueExpression.getValue(context);
    }

    /**
     * @return
     */
    public String getSearchExpr() {
        OIMContext oimContext = new OIMContext();
        boolean isLKDrift = oimContext.getCurrentUser().getRoles().contains("lokalitdrift");
        String searchExpr = getCatStateBean().getSearchExpr();
        if (isLKDrift && searchExpr != null && searchExpr.startsWith("publ1c ")) {
            searchExpr = searchExpr.substring(7);
        }
        return searchExpr;
    }

    public void setSearchExpr(String searchExpr) {
        OIMContext oimContext = new OIMContext();
        boolean isLKDrift = oimContext.getCurrentUser().getRoles().contains("lokalitdrift");
        if (isLKDrift)
            searchExpr = "publ1c " + searchExpr;
        getCatStateBean().setSearchExpr(searchExpr);
    }
}
