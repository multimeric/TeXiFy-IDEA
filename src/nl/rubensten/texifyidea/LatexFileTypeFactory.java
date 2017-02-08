package nl.rubensten.texifyidea;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import nl.rubensten.texifyidea.file.StyleFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sten Wessel
 */
public class LatexFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(LatexFileType.INSTANCE, "latex");
        consumer.consume(StyleFileType.INSTANCE, "latex-style");
    }
}