package com.hypnoticocelot.jaxrs.doclet.apidocs;

import com.sun.javadoc.RootDoc;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;

import java.io.IOException;

public class RootDocLoader {

    private RootDocLoader() {
    }

    public static RootDoc fromPath(String path, String subpackage) throws IOException {
        final Context context = new Context();
        Options.instance(context).put("-sourcepath", path);
        Messager.preRegister(context, "Messager!");

        final ListBuffer<String> subPackages = new ListBuffer<String>();
        subPackages.append(subpackage);

        final JavadocTool javaDoc = JavadocTool.make0(context);
        return javaDoc.getRootDocImpl(
                "",
                null,
                new ModifierFilter(ModifierFilter.ALL_ACCESS),
                new ListBuffer<String>().toList(),
                new ListBuffer<String[]>().toList(),
                false,
                subPackages.toList(),
                new ListBuffer<String>().toList(),
                false, false, false
        );
    }

}
