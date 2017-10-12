
#ifdef GL_ES
precision mediump float;
#endif


uniform vec4 u_globalAmbient;

uniform vec4 u_lightColor;

uniform vec4 u_materialDiffuse;
uniform vec4 u_materialSpecular;
uniform float u_materialShininess; 
uniform vec4 u_materialEmission;

varying vec4 v_normal;
varying vec4 v_s;
varying vec4 v_h;

void main()
{
	
	float lambert = max(0.0,dot(v_normal,v_s) / (length(v_normal)*length(v_s)));
	float phong = max(0.0,dot(v_normal,v_h) / (length(v_normal)*length(v_h)));
	
	vec4 diffuseColor = lambert * u_materialDiffuse * u_lightColor;
	vec4 specularColor = pow(phong, u_materialShininess) * u_materialSpecular * vec4(1,1,1,1);
	
	
	gl_FragColor =  u_globalAmbient * u_materialDiffuse + diffuseColor + specularColor + u_materialEmission; 
}