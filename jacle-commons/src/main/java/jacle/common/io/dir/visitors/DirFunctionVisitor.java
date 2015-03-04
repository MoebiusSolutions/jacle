package jacle.common.io.dir.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.common.base.Function;

public class DirFunctionVisitor extends SimpleFileVisitor<Path> {

    private Function<Path,FileVisitResult> directoryFunction;

    public DirFunctionVisitor(Function<Path, FileVisitResult> directoryFunction) {
        this.directoryFunction = directoryFunction;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return directoryFunction.apply(dir);
    }
}
