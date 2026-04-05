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

const val GLITCH_TEXT_SHADER = """
    uniform shader composable;
    uniform float2 size;
    uniform float time;

    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898, 78.233))) * 43758.5453123);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / size;
        
        // Define blocky horizontal bands
        float ySegment = floor(uv.y * 10.0); // 10 distinct horizontal slices
        float timeFrame = floor(time * 12.0); // Update glitch pattern 12 times a second
        
        float randVal = random(float2(ySegment, timeFrame));
        
        // Trigger a glitch heavily on roughly 15% of segments
        float isGlitch = step(0.85, randVal); 
        
        // Calculate the horizontal tearing offset
        float glitchOffset = (random(float2(ySegment, time)) - 0.5) * 0.2 * isGlitch;

        // Split RGB channels
        float2 rCoord = fragCoord + float2(glitchOffset * size.x, 0.0);
        float2 gCoord = fragCoord;
        float2 bCoord = fragCoord - float2(glitchOffset * 0.5 * size.x, 0.0);

        half r = composable.eval(rCoord).r;
        half g = composable.eval(gCoord).g;
        half b = composable.eval(bCoord).b;
        
        // Prevent bounding box cutoffs by checking the maximum alpha
        half a_r = composable.eval(rCoord).a;
        half a_g = composable.eval(gCoord).a;
        half a_b = composable.eval(bCoord).a;
        half maxAlpha = max(max(a_r, a_g), a_b);

        // Add some localized static snow inside the glitched block
        float staticNoise = (random(uv * time) - 0.5) * 1.5 * isGlitch * float(maxAlpha);
        
        float finalR = clamp(float(r) + staticNoise, 0.0, 1.0);
        float finalG = clamp(float(g) + staticNoise, 0.0, 1.0);
        float finalB = clamp(float(b) + staticNoise, 0.0, 1.0);

        // Tint static heavily green if it hits
        return half4(half(finalR), half(finalG), half(finalB), maxAlpha);
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.glitchTextEffect(): Modifier {
    val shader = remember { RuntimeShader(GLITCH_TEXT_SHADER) }

    val infiniteTransition = rememberInfiniteTransition(label = "glitch_time")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_time_anim",
    )

    return this.graphicsLayer {
        if (size.width > 0f && size.height > 0f) {
            shader.setFloatUniform("size", size.width, size.height)
            shader.setFloatUniform("time", time)

            renderEffect = RenderEffect
                .createRuntimeShaderEffect(shader, "composable")
                .asComposeRenderEffect()

            clip = true
        }
    }
}

@Composable
fun Modifier.glitchTextEffectIfSupported(): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        @Suppress("NewApi")
        this.glitchTextEffect()
    } else {
        this
    }
}
