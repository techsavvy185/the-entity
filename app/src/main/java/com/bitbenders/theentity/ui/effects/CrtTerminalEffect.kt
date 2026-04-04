package com.bitbenders.theentity.ui.effects

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
const val CRTCurvatureShader = """
    uniform float2 resolution;
    uniform float time;
    uniform shader contents; 

    // Very subtle barrel distortion
    float2 warp(float2 uv) {
        float2 delta = uv - 0.5;
        float delta2 = dot(delta, delta);
        float delta4 = delta2 * delta2;
        // Kept at absolute minimum just to give a slight rounded screen edge
        return uv + delta * (delta2 * 0.02 + delta4 * 0.03);
    }

    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / resolution;
        
        uv = warp(uv);

        // Removed the harsh horizontal screen tearing/wobble entirely. 
        // Coordinates are clean now.

        // Bounds check
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
            return half4(0.0, 0.0, 0.0, 1.0);
        }

        half4 color = contents.eval(uv * resolution);

        // 1. Scanlines
        // Thin, subtle, dark lines separating the pixels
        float scanline = sin(uv.y * 800.0) * 0.04;
        color.rgb -= scanline;

        // 2. Minimal Static Noise
        // Multiplier dropped from 0.18 down to 0.06. Just enough for a slight "film grain" texture.
        float noise = (random(uv * time) - 0.5) * 0.06;
        color.rgb += noise;

        // 3. Soft Phosphor Sweep (The Glow Line)
        // Drastically reduced intensity (0.03) so it doesn't wash out the green text into white.
        float scanBar = (sin(uv.y * 10.0 - time * 2.0) + 1.0) * 0.5;
        float glowLine = pow(scanBar, 30.0) * 0.03; 
        color.rgb += glowLine;

        // 4. Very Slight Power Pulse
        // A slow, almost imperceptible throb to make the screen feel "alive"
        float flicker = 1.0 + (sin(time * 15.0) * 0.015);
        color.rgb *= flicker;

        return color;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.crtTerminalEffect(): Modifier {
    // Compile the shader once per composition
    val shader = remember { RuntimeShader(CRTCurvatureShader) }

    // Infinite timer to drive noise / subtle animation
    val infiniteTransition = rememberInfiniteTransition(label = "crt_time")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "crt_time_anim",
    )

    return this.graphicsLayer {
        // Protect against zero-size during first composition
        if (size.width > 0f && size.height > 0f) {
            shader.setFloatUniform("resolution", size.width, size.height)
            shader.setFloatUniform("time", time)

            renderEffect = RenderEffect
                .createRuntimeShaderEffect(shader, "contents")
                .asComposeRenderEffect()

            // Ensure content is drawn into an offscreen buffer first
            clip = true
        }
    }
}

