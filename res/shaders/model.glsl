#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexId;

uniform mat4 uCameraMatrix;

out vec4 fColor;
out vec2 fTexCoords;
flat out int fTexId;

void main(){
	fColor = aColor;

	fTexCoords = aTexCoords;
	fTexId = int(aTexId);

	gl_Position = uCameraMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
flat in int fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main(){
	if (fTexId > 0) {
		color = texture(uTextures[fTexId], fTexCoords) * fColor;
	} else {
		color = fColor;
	}
}