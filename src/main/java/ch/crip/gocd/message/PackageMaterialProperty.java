package ch.crip.gocd.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PackageMaterialProperty {

    @Expose
    private String value;
    
    @Expose
    @SerializedName("display-order")
    private String displayOrder;    
    
    @Expose
    @SerializedName("display-name")
    private String displayName;
    
    @Expose
    @SerializedName("part-of-identity")
    private Boolean partOfIdentity;    

    @Expose
    private Boolean secure;

    @Expose
    private Boolean required;

    public PackageMaterialProperty withSecure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public PackageMaterialProperty withPartOfIdentity(Boolean partOfIdentity) {
        this.partOfIdentity = partOfIdentity;
        return this;
    }

    public PackageMaterialProperty withRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public PackageMaterialProperty withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public PackageMaterialProperty withDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }

    public PackageMaterialProperty withValue(String value) {
        this.value = value;
        return this;
    }

    public String value() {
        return value;
    }

    public Boolean secure() {
        return secure;
    }

    public Boolean partOfIdentity() {
        return partOfIdentity;
    }

    public Boolean required() {
        return required;
    }

    public String displayName() {
        return displayName;
    }

    public String displayOrder() {
        return displayOrder;
    }


}
