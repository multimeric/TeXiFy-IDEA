// This is a generated file. Not intended for manual editing.
package nl.hannahsten.texifyidea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import nl.hannahsten.texifyidea.psi.LatexKeyvalKey;
import nl.hannahsten.texifyidea.psi.LatexKeyvalValue;
import nl.hannahsten.texifyidea.psi.LatexStrictKeyvalPair;
import nl.hannahsten.texifyidea.psi.LatexVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatexStrictKeyvalPairImpl extends ASTWrapperPsiElement implements LatexStrictKeyvalPair {

  public LatexStrictKeyvalPairImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LatexVisitor visitor) {
    visitor.visitStrictKeyvalPair(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LatexVisitor) accept((LatexVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LatexKeyvalKey getKeyvalKey() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LatexKeyvalKey.class));
  }

  @Override
  @Nullable
  public LatexKeyvalValue getKeyvalValue() {
    return PsiTreeUtil.getChildOfType(this, LatexKeyvalValue.class);
  }

}
