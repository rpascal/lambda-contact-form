package graal.reflection;

import com.coldbrewcode.contact.model.ContactFormRequestBody;
import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@AutomaticFeature
public class GraalProgrammaticReflectionRegistration implements Feature {

    private static final List<Class<?>> classesWithZeroArgConstructor = Arrays.asList();

    /**
     * These classes should most likely have a JsonCreator property as well as JsonProperty to ensure jackson
     * can deserialize properly
     * <p>
     * For example:
     *
     * @AllArgsConstructor(onConstructor=@__(@JsonCreator))
     * @JsonProperty("")
     */
    private static final List<Class<?>> classesWithoutZeroArgConstructor = Arrays.asList(
            ContactFormRequestBody.class
    );


    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        classesWithZeroArgConstructor.forEach(c -> {
            RuntimeReflection.registerForReflectiveInstantiation(c);
        });

        Stream.concat(classesWithZeroArgConstructor.stream(), classesWithoutZeroArgConstructor.stream())
                .forEach(x -> registerAll(x));
    }

    public static void registerAll(Class<?> c) {
        RuntimeReflection.register(c);
        RuntimeReflection.register(c.getConstructors());
        RuntimeReflection.register(c.getDeclaredConstructors());
        RuntimeReflection.register(c.getFields());
        RuntimeReflection.register(c.getDeclaredFields());
        RuntimeReflection.register(c.getMethods());
        RuntimeReflection.register(c.getDeclaredMethods());
        RuntimeReflection.register(c.getDeclaredClasses());
    }


}
