package com.github.maxstepanovski.projecttreeplugin

import org.jetbrains.kotlin.idea.structuralsearch.resolveKotlinType
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtDeclaration.getFullName(): String =
        resolveKotlinType()?.constructor?.declarationDescriptor?.fqNameSafe?.asString().orEmpty()