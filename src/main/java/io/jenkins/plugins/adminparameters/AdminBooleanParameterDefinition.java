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
import hudson.model.BooleanParameterDefinition;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

import java.util.Objects;

public class AdminBooleanParameterDefinition extends BooleanParameterDefinition {

    private boolean onlyHidden;
    private boolean disableInfoInDesc;

    @DataBoundConstructor
    public AdminBooleanParameterDefinition(String name) {
        super(name);
    }

    public AdminBooleanParameterDefinition(String name, boolean defaultValue, String description, boolean onlyHidden,
                                           boolean disableInfoInDesc) {
        this(name);
        this.setDefaultValue(defaultValue);
        this.setDescription(description);
        this.setOnlyHidden(onlyHidden);
        this.setDisableInfoInDesc(disableInfoInDesc);
    }

    public AdminBooleanParameterDefinition(String name, boolean defaultValue, String description) {
        this(name);
        this.setDefaultValue(defaultValue);
        this.setDescription(description);
    }

    public AdminBooleanParameterDefinition(String name, boolean defaultValue) {
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
    public AdminBooleanParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
        if (defaultValue instanceof AdminBooleanParameterValue value) {
            return new AdminBooleanParameterDefinition(this.getName(), value.value, this.getDescription());
        } else {
            return this;
        }
    }

    @Override
    public AdminBooleanParameterValue getDefaultParameterValue() {
        return new AdminBooleanParameterValue(this.getName(), this.isDefaultValue(), this.getDescription());
    }

    @Override
    public ParameterValue createValue(StaplerRequest2 req, JSONObject jo) {
        if (!isUserAdmin()) {
            return onlyHidden ? getDefaultParameterValue() : null;
        }
        AdminBooleanParameterValue value = req.bindJSON(AdminBooleanParameterValue.class, jo);
        value.setDescription(this.getDescription());
        return value;
    }

    @Override
    public ParameterValue createValue(String value) {
        if (!isUserAdmin()) {
            return onlyHidden ? getDefaultParameterValue() : null;
        }
        return new AdminBooleanParameterValue(this.getName(), Boolean.parseBoolean(value), this.getDescription());
    }

    @Override
    public int hashCode() {
        return AdminBooleanParameterDefinition.class != this.getClass() ? super.hashCode() :
                Objects.hash(this.getName(), this.getDescription(), this.isDefaultValue(), this.isOnlyHidden(),
                        this.isDisableInfoInDesc());
    }

    @SuppressFBWarnings(
            value = {"EQ_GETCLASS_AND_CLASS_CONSTANT"},
            justification = "ParameterDefinitionTest tests that subclasses are not equal to their parent classes, so the behavior appears to be intentional"
    )
    @Override
    public boolean equals(Object obj) {
        if (AdminBooleanParameterDefinition.class != this.getClass()) {
            return super.equals(obj);
        } else if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            AdminBooleanParameterDefinition other = (AdminBooleanParameterDefinition) obj;
            if (!Objects.equals(this.getName(), other.getName())) {
                return false;
            } else if (!Objects.equals(this.getDescription(), other.getDescription())) {
                return false;
            } else if (!Objects.equals(this.isDefaultValue(), other.isDefaultValue())) {
                return false;
            } else if (this.isOnlyHidden() != other.isOnlyHidden()) {
                return false;
            } else {
                return this.isDisableInfoInDesc() == other.isDisableInfoInDesc();
            }
        }
    }

    @Extension
    @Symbol({"adminBoolean", "adminBooleanParam"})
    public static class DescriptorImpl extends ParameterDefinition.ParameterDescriptor {
        public DescriptorImpl() {
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Admin Boolean Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/admin-params/help/parameter/admin-bool-param.html";
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
