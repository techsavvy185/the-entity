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

// CRT curvature + scanline + noise shader
const val CRTCurvatureShader = """
    uniform float2 resolution;
    uniform float time;
    uniform shader contents; // Your Compose UI

    // 1. Minimal Curvature
    float2 warp(float2 uv) {
        float2 delta = uv - 0.5;
        float delta2 = dot(delta, delta);
        float delta4 = delta2 * delta2;
        // Drastically reduced the multipliers (0.02 and 0.04) to make the screen almost flat
        return uv + delta * (delta2 * 0.02 + delta4 * 0.04);
    }

    // Pseudo-random noise generator
    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
    }

    half4 main(float2 fragCoord) {
        // Normalize coordinates from 0.0 to 1.0
        float2 uv = fragCoord / resolution;
        
        // Apply curvature
        uv = warp(uv);

        // 2. Horizontal Sync (Screen Tearing & Wobble)
        // Adds a subtle wave, plus an occasional sharp horizontal jump
        float wobble = sin(uv.y * 40.0 + time * 10.0) * 0.001; 
        float tear = step(0.99, sin(uv.y * 15.0 - time * 2.0)) * 0.008;
        uv.x += wobble + tear; // Must be applied BEFORE grabbing the pixel

        // Bounds check: paint black if off-screen
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
            return half4(0.0, 0.0, 0.0, 1.0);
        }

        // Grab the actual pixel color from your Compose UI at the warped, torn coordinate
        half4 color = contents.eval(uv * resolution);

        // 3. Scanlines (Kept the same)
        float scanline = sin(uv.y * 800.0) * 0.04;
        color.rgb -= scanline;

        // 4. More Static Noise
        // Multiplier bumped from 0.05 to 0.18 for a much harsher, snowier look
        float noise = (random(uv * time) - 0.5) * 0.18;
        color.rgb += noise;

        // 5. Glow Line (Sweeping Bar)
        // Creates a bright horizontal band that continuously rolls down the screen
        float scanBar = (sin(uv.y * 10.0 - time * 4.0) + 1.0) * 0.5;
        float glowLine = pow(scanBar, 30.0) * 0.15; // pow() makes the line sharp instead of muddy
        color.rgb += glowLine;

        // 6. Flickering
        // Modulates the entire screen's brightness rapidly to simulate failing power
        float flicker = 1.0 + (sin(time * 45.0) * 0.03) + (sin(time * 80.0) * 0.02);
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

