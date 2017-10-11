
#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

uniform vec4 u_lightPosition;
uniform vec4 u_lightDiffuse;
uniform vec4 u_materialDiffuse;

varying vec4 v_color;

void main()
{
	vec4 position = vec4(a_position.x, a_position.y, a_position.z, 1.0);
	position = u_modelMatrix * position;

	vec4 normal = vec4(a_normal.x, a_normal.y, a_normal.z, 0.0);
	normal = u_modelMatrix * normal;

	//Lighting
	vec4 s = u_lightPosition - position;
	float lambert = dot(normal,s) / (length(normal)*length(s));
	
	v_color = lambert * u_materialDiffuse * u_lightDiffuse;

	position = u_viewMatrix * position;
	//normal = u_viewMatrix * normal;

	//v_color = (dot(normal, vec4(0,0,1,0)) / length(normal)) * u_color;
	

	gl_Position = u_projectionMatrix * position;
}