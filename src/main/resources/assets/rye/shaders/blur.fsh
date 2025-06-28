#version 150

uniform sampler2D Sampler0;
uniform vec2 u_direction;
uniform vec2 u_resolution;
uniform float u_radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 texelSize = 1.0 / u_resolution;
    vec4 color = vec4(0.0);
    float totalWeight = 0.0;

    int samples = int(u_radius * 2.0 + 1.0);
    float sigma = u_radius / 3.0;
    float twoSigmaSquared = 2.0 * sigma * sigma;

    for (int i = -samples; i <= samples; i++) {
        vec2 offset = u_direction * float(i) * texelSize;
        float weight = exp(-float(i * i) / twoSigmaSquared);

        color += texture(Sampler0, texCoord + offset) * weight;
        totalWeight += weight;
    }

    fragColor = color / totalWeight;
}