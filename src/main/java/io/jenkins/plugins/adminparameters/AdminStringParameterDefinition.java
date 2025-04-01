/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 Tunahan Sezen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jenkins.plugins.adminparameters;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.StringParameterDefinition;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

import java.util.Objects;

public class AdminStringParameterDefinition extends StringParameterDefinition {

    private boolean onlyHidden;
    private boolean disableInfoInDesc;

    @DataBoundConstructor
    public AdminStringParameterDefinition(String name) {
        super(name);
    }

    public AdminStringParameterDefinition(String name, String defaultValue, String description, boolean trim,
                                          boolean onlyHidden, boolean disableInfoInDesc) {
        this(name);
        this.setDefaultValue(defaultValue);
        this.setDescription(description);
        this.setTrim(trim);
        this.setOnlyHidden(onlyHidden);
        this.setDisableInfoInDesc(disableInfoInDesc);
    }

    public AdminStringParameterDefinition(String name, String defaultValue, String description) {
        this(name);
        this.setDefaultValue(defaultValue);
        this.setDescription(description);
    }

    public AdminStringParameterDefinition(String name, String defaultValue) {
        this(name);
        this.setDefaultValue(defaultValue);
    }

    public boolean isOnlyHidden() {
        return onlyHidden;
    }

    @DataBoundSetter
    public void setOnlyHidden(boolean onlyHidden) {
        this.onlyHidden = onlyHidden;
    }

    public boolean isDisableInfoInDesc() {
        return disableInfoInDesc;
    }

    @DataBoundSetter
    public void setDisableInfoInDesc(boolean disableInfoInDesc) {
        this.disableInfoInDesc = disableInfoInDesc;
    }

    @Override
    public AdminStringParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof AdminStringParameterValue value) {
            return new AdminStringParameterDefinition(this.getName(), value.value, this.getDescription());
        } else {
            return this;
        }
    }

    @Override
    public AdminStringParameterValue getDefaultParameterValue() {
        AdminStringParameterValue value = new AdminStringParameterValue(this.getName(), this.getDefaultValue(),
                this.getDescription());
        if (this.isTrim()) {
            value.doTrim();
        }
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest2 req, JSONObject jo) {
        if (!isUserAdmin()) {
            return onlyHidden ? getDefaultParameterValue() : null;
        }
        AdminStringParameterValue value = req.bindJSON(AdminStringParameterValue.class, jo);
        if (this.isTrim()) {
            value.doTrim();
        }
        value.setDescription(this.getDescription());
        return value;
    }

    @Override
    public ParameterValue createValue(String str) {
        if (!isUserAdmin()) {
            return onlyHidden ? getDefaultParameterValue() : null;
        }
        AdminStringParameterValue value = new AdminStringParameterValue(this.getName(), str, this.getDescription());
        if (this.isTrim()) {
            value.doTrim();
        }
        return value;
    }

    @Override
    public int hashCode() {
        return AdminStringParameterDefinition.class != this.getClass() ? super.hashCode() :
                Objects.hash(this.getName(), this.getDescription(), this.getDefaultValue(), this.isTrim(),
                        this.isOnlyHidden(), this.isDisableInfoInDesc());
    }

    @SuppressFBWarnings(
            value = {"EQ_GETCLASS_AND_CLASS_CONSTANT"},
            justification = "ParameterDefinitionTest tests that subclasses are not equal to their parent classes, so the behavior appears to be intentional"
    )
    @Override
    public boolean equals(Object obj) {
        if (AdminStringParameterDefinition.class != this.getClass()) {
            return super.equals(obj);
        } else if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            AdminStringParameterDefinition other = (AdminStringParameterDefinition) obj;
            if (!Objects.equals(this.getName(), other.getName())) {
                return false;
            } else if (!Objects.equals(this.getDescription(), other.getDescription())) {
                return false;
            } else if (!Objects.equals(this.getDefaultValue(), other.getDefaultValue())) {
                return false;
            } else if (!Objects.equals(this.isTrim(), other.isTrim())) {
                return this.isTrim() == other.isTrim();
            } else if (this.isOnlyHidden() != other.isOnlyHidden()) {
                return false;
            } else {
                return this.isDisableInfoInDesc() == other.isDisableInfoInDesc();
            }
        }
    }

    @Extension
    @Symbol({"adminString", "adminStringParam"})
    public static class DescriptorImpl extends ParameterDefinition.ParameterDescriptor {
        public DescriptorImpl() {
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Admin String Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/admin-params/help/parameter/admin-string-param.html";
        }
    }

    public boolean isUserAdmin() {
        return Utils.isUserAdmin();
    }

    public String adminFormattedDescription() {
        if (disableInfoInDesc) {
            return getFormattedDescription();
        }
        return Utils.adminFormattedDescription(getFormattedDescription());
    }
}
