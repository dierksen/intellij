/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.scope.output;

import com.google.idea.blaze.base.scope.BlazeContext;
import com.google.idea.blaze.base.scope.Output;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.Navigatable;
import java.io.File;
import javax.annotation.Nullable;

/** An issue in a blaze operation. */
public class IssueOutput implements Output {

  static final int NO_LINE = -1;
  static final int NO_COLUMN = -1;

  @Nullable private final File file;
  private final int line;
  private final int column;
  private final Category category;
  private final String message;
  @Nullable private final Navigatable navigatable;
  @Nullable private final TextRange consoleHyperlinkRange;

  /** Issue category */
  public enum Category {
    ERROR,
    WARNING,
    NOTE,
    STATISTICS,
    INFORMATION,
  }

  public static Builder issue(Category category, String message) {
    return new Builder(category, message);
  }

  public static Builder error(String message) {
    return new Builder(Category.ERROR, message);
  }

  public static Builder warn(String message) {
    return new Builder(Category.WARNING, message);
  }

  /** Builder for an issue */
  public static class Builder {
    private final Category category;
    private final String message;
    @Nullable private File file;
    private int line = NO_LINE;
    private int column = NO_COLUMN;
    @Nullable Navigatable navigatable;
    @Nullable private TextRange consoleHyperlinkRange;

    public Builder(Category category, String message) {
      this.category = category;
      this.message = message;
    }

    public Builder inFile(@Nullable File file) {
      this.file = file;
      return this;
    }

    public Builder onLine(int line) {
      this.line = line;
      return this;
    }

    public Builder inColumn(int column) {
      this.column = column;
      return this;
    }

    public Builder navigatable(@Nullable Navigatable navigatable) {
      this.navigatable = navigatable;
      return this;
    }

    public Builder consoleHyperlinkRange(@Nullable TextRange consoleHyperlinkRange) {
      this.consoleHyperlinkRange = consoleHyperlinkRange;
      return this;
    }

    public IssueOutput build() {
      return new IssueOutput(
          file, line, column, navigatable, consoleHyperlinkRange, category, message);
    }

    public void submit(BlazeContext context) {
      context.output(build());
      if (category == Category.ERROR) {
        context.setHasError();
      }
    }
  }

  private IssueOutput(
      @Nullable File file,
      int line,
      int column,
      @Nullable Navigatable navigatable,
      @Nullable TextRange consoleHyperlinkRange,
      Category category,
      String message) {
    this.file = file;
    this.line = line;
    this.column = column;
    this.navigatable = navigatable;
    this.consoleHyperlinkRange = consoleHyperlinkRange;
    this.category = category;
    this.message = message;
  }

  @Nullable
  public File getFile() {
    return file;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  @Nullable
  public Navigatable getNavigatable() {
    return navigatable;
  }

  @Nullable
  public TextRange getConsoleHyperlinkRange() {
    return consoleHyperlinkRange;
  }

  public Category getCategory() {
    return category;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }
}
