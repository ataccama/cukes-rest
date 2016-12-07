package lv.ctco.cukescore.internal;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import lv.ctco.cukescore.CukesCorePlugin;
import lv.ctco.cukescore.CukesOptions;
import lv.ctco.cukescore.CukesRuntimeException;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Properties;

public abstract class GuiceModule extends AbstractModule {

    protected void bindInterceptor(MethodInterceptor interceptor, Class<? extends Annotation> annotationType) {
        requestInjection(interceptor);
        bindInterceptor(Matchers.annotatedWith(annotationType), Matchers.any(), interceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(annotationType), interceptor);
    }

    @SuppressWarnings("unchecked")
    protected void bindPlugins() {
        try {
            Multibinder<CukesCorePlugin> multibinder = Multibinder.newSetBinder(binder(), CukesCorePlugin.class);
            ClassLoader classLoader = GuiceModule.class.getClassLoader();
            Properties prop = new Properties();
            URL url = createCukesPropertyFileUrl(classLoader);
            if (url == null) return;
            prop.load(url.openStream());
            String pluginsArr = prop.getProperty(CukesOptions.PROPERTIES_PREFIX + CukesOptions.PLUGINS);
            if (pluginsArr == null) return;
            String[] pluginClasses = pluginsArr.split(CukesOptions.DELIMITER);
            for (String pluginClass : pluginClasses) {
                Class<? extends CukesCorePlugin> aClass = (Class<? extends CukesCorePlugin>) classLoader.loadClass(pluginClass);
                multibinder.addBinding().to(aClass);
            }
        } catch (Exception e) {
            throw new CukesRuntimeException("Binding of CukesRest plugins failed");
        }
    }

    private URL createCukesPropertyFileUrl(final ClassLoader classLoader) {
        String cukesProfile = System.getProperty("cukes.profile");
        String propertiesFileName = cukesProfile == null || cukesProfile.isEmpty()
            ? "cukes.properties"
            : "cukes-" + cukesProfile + ".properties";
        return classLoader.getResource(propertiesFileName);
    }
}
