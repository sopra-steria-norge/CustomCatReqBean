package no.steria.tad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.rich.context.AdfFacesContext;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcInvalidAttributeException;
import Thor.API.Exceptions.tcInvalidLookupException;
import Thor.API.Exceptions.tcInvalidValueException;
import Thor.API.Operations.tcLookupOperationsIntf;

import java.util.Arrays;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.input.RichInputListOfValues;

import oracle.adfinternal.view.faces.model.binding.FacesCtrlLOVBinding;

import oracle.iam.platform.OIMClient;
import oracle.iam.platform.Platform;
import oracle.iam.ui.catalog.view.backing.CatStateBean;
import oracle.iam.ui.platform.utils.FacesUtils;
import oracle.iam.ui.platform.view.backing.BaseMB;

import org.apache.myfaces.trinidad.component.UIXComponent;
import org.apache.myfaces.trinidad.component.UIXValue;

public class OrgItemStateBean extends BaseMB{
    private String dependant = "";
    private Object fctx;

    public OrgItemStateBean() {
        super();
    }

    public String getRegion() {
        return this.dependant;
    }
    
    public void setRegion(String paramRegion) {
        this.dependant = paramRegion;
    }
    
    public List<SelectItem> getRegions() {
        return this.getDependant("Lookup.TAD.Regions");
    }

    public Map<String,List<SelectItem>> getDependants() {
        return new HashMap<String,List<SelectItem>>() {
                @Override
                public List<SelectItem> get(Object key) {
                    return getDependant((String)key);
                }
            };
    }

    public List<SelectItem> getDependant(String lookup) {
       List<SelectItem> dependants = new ArrayList<SelectItem>();
        try {
            OIMClient client = new OIMClient();
            tcLookupOperationsIntf lookupTypeService = client.getService(tcLookupOperationsIntf.class);
            Thor.API.tcResultSet rs = lookupTypeService.getLookupValues(lookup);
            int rowCount = rs.getTotalRowCount();
            String codes[] =  new String[rowCount];
            for (int i = 0; i < rowCount; i++) {
                rs.goToRow(i);
                codes[i] = rs.getStringValueFromColumn(2);
            }
            Arrays.sort(codes);
            for (int i = 0; i < rowCount; i++) {
                dependants.add(new SelectItem(codes[i], codes[i]));
            }

        } catch (tcAPIException e) {
            dependants.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (tcInvalidLookupException e) {
            dependants.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (tcColumnNotFoundException e) {
            dependants.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (Throwable e) {
            dependants.add(new SelectItem(e.getMessage(),e.getMessage()));
        }
        return dependants;
    }

    public List<SelectItem> getDrives() {
        return getGroups("FGroup");
    }

    public Map<String,List<SelectItem>> getGroups() {
     //nitializeDependant();
        return new HashMap<String,List<SelectItem>>() {
            @Override
            public List<SelectItem> get(Object key) {
                return getGroups((String)key);
            }
        };
    }

    public List<SelectItem> getGroups(String lookup) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        try {
            OIMClient client = new OIMClient();
            tcLookupOperationsIntf lookupTypeService = client.getService(tcLookupOperationsIntf.class);
            HashMap search = new HashMap();
            FacesContext fctx = FacesContext.getCurrentInstance();
            ELContext elctx = fctx.getELContext();
            Application application = fctx.getApplication();
            ExpressionFactory exprFactory = application.getExpressionFactory(); 
            ValueExpression valueExpr = exprFactory.createValueExpression(elctx,"#{bindings.lov_ds_act_key__c}",Object.class);
            FacesCtrlLOVBinding fclb =  (FacesCtrlLOVBinding)valueExpr.getValue(elctx);
            search.put("Lookup Definition.Lookup Code Information.Decode",fclb.getInputValue().toString());
            Thor.API.tcResultSet rs = lookupTypeService.getLookupValues(lookup,search);
            int rowCount = rs.getTotalRowCount();
            String codes[] =  new String[rowCount];
            for (int i = 0; i < rowCount; i++) {
                rs.goToRow(i);
                codes[i] = rs.getStringValueFromColumn(2);
            }
            Arrays.sort(codes);
            for (int i = 0; i < rowCount; i++) {
                result.add(new SelectItem(codes[i], codes[i]));
            }
            /*
            String code = null;
            for (int i = 0; i < rowCount; i++) {
                rs.goToRow(i);
                code = rs.getStringValueFromColumn(2);
                result.add( new SelectItem(code, code));
            }
        */
        } catch (tcAPIException e) {
            result.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (tcInvalidLookupException e) {
            result.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (tcColumnNotFoundException e) {
            result.add(new SelectItem(e.getMessage(),e.getMessage()));
        } catch (Throwable e) {
            result.add(new SelectItem(e.getMessage(),e.getMessage()));
        }
        return result;
    }

    public void regionChangeListener(ValueChangeEvent vce) {
        this.dependant = vce.getNewValue().toString();
    }
    public void dependantChangeListener(ValueChangeEvent vce) {
        this.dependant = vce.getNewValue().toString();
        
        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ELContext context = fctx.getELContext();   
        MethodExpression originalActionListener =expressionFactory.createMethodExpression(context,"#{pageFlowScope.cartDetailStateBean.attributeValueChangedListener}", null, new Class [] { ValueChangeEvent.class });
        originalActionListener.invoke(context, new Object[] {vce});
        //FacesUtils.getMethodExpressionFromEL("#{pageFlowScope.cartDetailStateBean.attributeValueChangedListener}").invoke(FacesContext.getCurrentInstance().getELContext(), new Object[] {vce});
    }
    
    public void initializeDependant() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        UIViewRoot root = fctx.getViewRoot();
        UIXValue organisationField = (UIXValue)root.findComponent("_xg_45");
        this.dependant = organisationField.getValue().toString();
    }
    
}
