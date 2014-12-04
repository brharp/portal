package ca.uoguelph.ccs.portal.mycourses;

public class Course
{
    private String code;
    private String websiteUrl;
    private String resourcesUrl;
    private String classListUrl;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getWebsiteUrl()
    {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl)
    {
        this.websiteUrl = websiteUrl;
    }

    public String getResourcesUrl()
    {
        return resourcesUrl;
    }

    public void setResourcesUrl(String resourcesUrl)
    {
        this.resourcesUrl = resourcesUrl;
    }

    public String getClassListUrl()
    {
        return classListUrl;
    }

    public void setClassListUrl(String classListUrl)
    {
        this.classListUrl = classListUrl;
    }
}
