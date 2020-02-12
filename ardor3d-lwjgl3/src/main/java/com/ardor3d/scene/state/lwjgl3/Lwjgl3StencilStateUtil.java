/**
 * Copyright (c) 2008-2020 Bird Dog Games, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <https://git.io/fjRmv>.
 */

package com.ardor3d.scene.state.lwjgl3;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL14C;
import org.lwjgl.opengl.GL20C;

import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.RenderContext;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.StencilState;
import com.ardor3d.renderer.state.StencilState.StencilFunction;
import com.ardor3d.renderer.state.StencilState.StencilOperation;
import com.ardor3d.renderer.state.record.StencilStateRecord;

public abstract class Lwjgl3StencilStateUtil {

    public static void apply(final StencilState state) {
        // ask for the current state record
        final RenderContext context = ContextManager.getCurrentContext();
        final StencilStateRecord record = (StencilStateRecord) context.getStateRecord(StateType.Stencil);
        context.setCurrentState(StateType.Stencil, state);

        setEnabled(state.isEnabled(), record);
        if (state.isEnabled()) {
            if (state.isUseTwoSided()) {
                GL20C.glStencilMaskSeparate(GL11C.GL_BACK, state.getStencilWriteMaskBack());

                GL20C.glStencilFuncSeparate(GL11C.GL_BACK, //
                        getGLStencilFunction(state.getStencilFunctionBack()), //
                        state.getStencilReferenceBack(), //
                        state.getStencilFuncMaskBack());
                GL20C.glStencilOpSeparate(GL11C.GL_BACK, //
                        getGLStencilOp(state.getStencilOpFailBack()), //
                        getGLStencilOp(state.getStencilOpZFailBack()), //
                        getGLStencilOp(state.getStencilOpZPassBack()));

                GL20C.glStencilMaskSeparate(GL11C.GL_FRONT, state.getStencilWriteMaskFront());
                GL20C.glStencilFuncSeparate(GL11C.GL_FRONT, //
                        getGLStencilFunction(state.getStencilFunctionFront()), //
                        state.getStencilReferenceFront(), //
                        state.getStencilFuncMaskFront());
                GL20C.glStencilOpSeparate(GL11C.GL_FRONT, //
                        getGLStencilOp(state.getStencilOpFailFront()), //
                        getGLStencilOp(state.getStencilOpZFailFront()), //
                        getGLStencilOp(state.getStencilOpZPassFront()));
            } else {
                GL11C.glStencilMask(state.getStencilWriteMaskFront());
                GL11C.glStencilFunc( //
                        getGLStencilFunction(state.getStencilFunctionFront()), //
                        state.getStencilReferenceFront(), //
                        state.getStencilFuncMaskFront());
                GL11C.glStencilOp( //
                        getGLStencilOp(state.getStencilOpFailFront()), //
                        getGLStencilOp(state.getStencilOpZFailFront()), //
                        getGLStencilOp(state.getStencilOpZPassFront()));
            }
        }

        if (!record.isValid()) {
            record.validate();
        }
    }

    private static int getGLStencilFunction(final StencilFunction function) {
        switch (function) {
            case Always:
                return GL11C.GL_ALWAYS;
            case Never:
                return GL11C.GL_NEVER;
            case EqualTo:
                return GL11C.GL_EQUAL;
            case NotEqualTo:
                return GL11C.GL_NOTEQUAL;
            case GreaterThan:
                return GL11C.GL_GREATER;
            case GreaterThanOrEqualTo:
                return GL11C.GL_GEQUAL;
            case LessThan:
                return GL11C.GL_LESS;
            case LessThanOrEqualTo:
                return GL11C.GL_LEQUAL;
        }
        throw new IllegalArgumentException("unknown function: " + function);
    }

    private static int getGLStencilOp(final StencilOperation operation) {
        switch (operation) {
            case Keep:
                return GL11C.GL_KEEP;
            case DecrementWrap:
                return GL14C.GL_DECR_WRAP;
            case Decrement:
                return GL11C.GL_DECR;
            case IncrementWrap:
                return GL14C.GL_INCR_WRAP;
            case Increment:
                return GL11C.GL_INCR;
            case Invert:
                return GL11C.GL_INVERT;
            case Replace:
                return GL11C.GL_REPLACE;
            case Zero:
                return GL11C.GL_ZERO;
        }
        throw new IllegalArgumentException("unknown operation: " + operation);
    }

    private static void setEnabled(final boolean enable, final StencilStateRecord record) {
        if (enable && (!record.isValid() || !record.enabled)) {
            GL11C.glEnable(GL11C.GL_STENCIL_TEST);
        } else if (!enable && (!record.isValid() || record.enabled)) {
            GL11C.glDisable(GL11C.GL_STENCIL_TEST);
        }

        record.enabled = enable;
    }

}
