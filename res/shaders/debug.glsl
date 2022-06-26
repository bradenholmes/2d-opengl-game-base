#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec3 aCol;

uniform mat4 uCameraMatrix;

out vec3 fCol;


void main(){
	fCol = aCol;
	gl_Position = uCameraMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec3 fCol;

out vec4 color;

void main(){
	color = new vec4(fCol, 1.0);
}
