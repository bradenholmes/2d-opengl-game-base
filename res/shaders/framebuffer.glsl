#type vertex
#version 330 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aTexCoords;

out vec2 fTexCoords;

void main(){
	fTexCoords = aTexCoords;

	gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
}

#type fragment
#version 330 core

in vec2 fTexCoords;

uniform sampler2D mainTexture;

out vec4 color;

void main(){

	vec4 world = texture(mainTexture, fTexCoords);
	color = world;


}
