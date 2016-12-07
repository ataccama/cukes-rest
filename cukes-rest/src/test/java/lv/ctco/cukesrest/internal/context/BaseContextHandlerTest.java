package lv.ctco.cukesrest.internal.context;

import lv.ctco.cukescore.internal.context.ContextCapturer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BaseContextHandlerTest {

    @InjectMocks
    ContextCapturer capturer;

    @Test
    public void shouldExtractNoGroupsInPattern() throws Exception {
        List<String> groups = capturer.extractGroups("(hello)");
        assertThat(groups, is(empty()));
    }

    @Test
    public void shouldExtractSingleGroupInPattern() throws Exception {
        List<String> groups = capturer.extractGroups("{(hello)}");
        assertThat(groups, contains("hello"));
    }

    @Test
    public void shouldExtractTwoGroupsInPattern() throws Exception {
        List<String> groups = capturer.extractGroups("{(hello)}, {(world)}");
        assertThat(groups, contains("hello", "world"));
    }

    @Test
    public void shouldNotExtractGroupsInPatternWithSpacesInName() throws Exception {
        List<String> groups = capturer.extractGroups("{(hello world)}");
        assertThat(groups, is(empty()));
    }

    @Test
    public void shouldExtractGroupsInPatternWithUnderscoreInName() throws Exception {
        List<String> groups = capturer.extractGroups("{(hello_world)}");
        assertThat(groups, contains("hello_world"));
    }

    @Test
    public void shouldExtractDotSeparatedName() throws Exception {
        List<String> groups = capturer.extractGroups("{(hello.world)}");
        assertThat(groups, contains("hello.world"));
    }
}
