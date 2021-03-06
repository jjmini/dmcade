package com.glority.quality.metrics.cpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.glority.quality.Constants.MetricCompareMode;
import com.glority.quality.Constants.MetricName;
import com.glority.quality.Constants.MetricResult;
import com.glority.quality.Constants.MetricType;
import com.glority.quality.metrics.MetricsCheckBaseTask;
import com.glority.quality.model.Metric;

/**
 * check cpp coverage report.
 */
public class CppBullseyeCheckTask extends MetricsCheckBaseTask {
    public static final String BULLSEYE_COV_CONDITION = "cd_cov";
    public static final String BULLSEYE_COV_FUNCTION = "fn_cov";
    public static final String BULLSEYE_COV_DECISION = "d_cov";
    public static final String BULLSEYE_TOTAL_CONDITION = "cd_total";
    public static final String BULLSEYE_TOTAL_DECISION = "d_total";
    public static final String BULLSEYE_TOTAL_FUNCTION = "fn_total";
    public static final int HUNDRED = 100;

    private float metricCondition;
    private float metricFunction;
    private float metricDecision;

    @Override
    public List<Metric> getMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric function = new Metric();
        String metricName = MetricName.CPP_BULLSEYE_COVERAGE_FUNCTION
                .toString();
        String thresholdString = getThreshold(metricName);
        function.setName(metricName);
        function.setNewValue(metricFunction);
        function.setOldValue(thresholdString);
        function.setType(MetricType.METRIC_TYPE_COVERAGE);

        if (isFloat(thresholdString)) {
            float oldValue = Float.parseFloat(thresholdString);
            function.setResult(checkMetric(oldValue, metricFunction,
                    MetricCompareMode.LARGER_BETTER));
        } else if (thresholdString != null) {
            function.setResult(MetricResult.METRIC_INVALIDTHRESHOLD);
        } else {
            function.setResult(MetricResult.METRIC_NULL);
        }
        metrics.add(function);

        Metric condition = new Metric();
        condition
                .setName(MetricName.CPP_BULLSEYE_COVERAGE_CONDITION.toString());
        condition.setNewValue(metricCondition);
        condition.setType(MetricType.METRIC_TYPE_COVERAGE);
        condition.setResult(MetricResult.METRIC_NOCHECK);
        metrics.add(condition);

        Metric decision = new Metric();
        decision.setName(MetricName.CPP_BULLSEYE_COVERAGE_DECISION.toString());
        decision.setNewValue(metricDecision);
        decision.setType(MetricType.METRIC_TYPE_COVERAGE);
        decision.setResult(MetricResult.METRIC_NOCHECK);
        metrics.add(decision);

        return metrics;
    }

    @Override
    public void parseReport() {
        try {
            Document doc = (new SAXBuilder()).build(new File(getReportPath()));
            Element reportRoot = doc.getRootElement();
            Element foldElement = (Element) reportRoot.getChildren().get(0);
            String covFunctionValue = foldElement
                    .getAttributeValue(BULLSEYE_COV_FUNCTION);
            String totalFunctionValue = foldElement
                    .getAttributeValue(BULLSEYE_TOTAL_FUNCTION);
            String covConditionValue = foldElement
                    .getAttributeValue(BULLSEYE_COV_CONDITION);
            String totalConditionValue = foldElement
                    .getAttributeValue(BULLSEYE_TOTAL_CONDITION);
            String covDecisionValue = foldElement
                    .getAttributeValue(BULLSEYE_COV_DECISION);
            String totalDecisionValue = foldElement
                    .getAttributeValue(BULLSEYE_TOTAL_DECISION);

            metricFunction = Float.parseFloat(covFunctionValue) * HUNDRED
                    / Float.parseFloat(totalFunctionValue);
            metricCondition = Float.parseFloat(covConditionValue) * HUNDRED
                    / Float.parseFloat(totalConditionValue);
            metricDecision = Float.parseFloat(covDecisionValue) * HUNDRED
                    / Float.parseFloat(totalDecisionValue);

        } catch (Exception e) {
            getProject().log("Failed to parser bullseye report");
            e.printStackTrace();
            throw new BuildException(e.getMessage(), e);
        }
    }

    public float getMetricFunction() {
        return metricFunction;
    }

    public void setMetricFunction(float metricFunction) {
        this.metricFunction = metricFunction;
    }

    public float getMetricCondition() {
        return metricCondition;
    }

    public void setMetricCondition(float metricCondition) {
        this.metricCondition = metricCondition;
    }

    public float getMetricDecision() {
        return metricDecision;
    }

    public void setMetricDecision(float metricDecision) {
        this.metricDecision = metricDecision;
    }

}
