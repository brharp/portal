package ca.uoguelph.ccs.portal.launchpad;

import java.util.ArrayList;
import java.util.Arrays;

public class MockLaunchPadService implements LaunchPadService 
{
    public LaunchPad getLaunchPad (String id) {
        // BIO101
        Link bioNotes = new Link();
        bioNotes.setUrl("notes.html");
        bioNotes.setDescription("Course _notes_");
        Link bioResource = new Link();
        bioResource.setUrl("resources.html");
        bioResource.setDescription("Library _resources_");
        Topic bio101 = new Topic();
        bio101.setDescription("BIO101");
        bio101.setLinks(Arrays.asList(new Link[]{bioNotes,bioResource}));

        // UNIV1200: Post Literacy
        Link litSite = new Link();
        litSite.setUrl("univ1200/course.html");
        litSite.setDescription("Course _website_");
        Link litResource = new Link();
        litResource.setUrl("univ1200/resources.html");
        litResource.setDescription("Library _resources_");
        Topic univ1200 = new Topic();
        univ1200.setDescription("_UNIV1200_: Post Literacy");
        univ1200.setLinks(Arrays.asList(new Link[]{litSite,litResource}));
        univ1200.setUrl("courses/univ1200.html");

        LaunchPad pad = new LaunchPad();
        pad.setName("MyCourses");
        pad.setTopics(Arrays.asList(new Topic[]{bio101,univ1200}));
        return pad;
    }
}
