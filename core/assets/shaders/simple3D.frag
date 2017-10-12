
#ifdef GL_ES
precision mediump float;
#endif


uniform vec4 u_globalAmbient;

uniform vec4 u_lightColor;
uniform vec4 u_lightColor2;
uniform vec4 u_dirLightColor;

uniform vec4 u_materialDiffuse;
uniform vec4 u_materialSpecular;
uniform float u_materialShininess; 
uniform vec4 u_materialEmission;

varying vec4 v_normal;
varying vec4 v_s;
varying vec4 v_h;
varying vec4 v_s2;
varying vec4 v_h2;
varying vec4 v_s3;
varying vec4 v_h3;

void main()
{
	
	float lambert = max(0.0,dot(v_normal,v_s) / (length(v_normal)*length(v_s)));
	float phong = max(0.0,dot(v_normal,v_h) / (length(v_normal)*length(v_h)));
	vec4 diffuseColor = lambert * u_materialDiffuse * u_lightColor;
	vec4 specularColor = pow(phong, u_materialShininess) * u_materialSpecular * vec4(1,1,1,1);
	
	vec4 light1 = diffuseColor + specularColor;
	
	
	lambert = max(0.0,dot(v_normal,v_s2) / (length(v_normal)*length(v_s2)));
	phong = max(0.0,dot(v_normal,v_h2) / (length(v_normal)*length(v_h2)));
	diffuseColor = lambert * u_materialDiffuse * u_lightColor2;
	specularColor = pow(phong, u_materialShininess) * u_materialSpecular * vec4(1,1,1,1);
	
	vec4 light2 = diffuseColor + specularColor;
	
	
	lambert = max(0.0,dot(v_normal,v_s3) / (length(v_normal)*length(v_s3)));
	phong = max(0.0,dot(v_normal,v_h3) / (length(v_normal)*length(v_h3)));
	diffuseColor = lambert * u_materialDiffuse * u_dirLightColor;
	specularColor = pow(phong, u_materialShininess) * u_materialSpecular * vec4(1,1,1,1);
	
	vec4 light3 = diffuseColor + specularColor;
	
	gl_FragColor =  u_globalAmbient * u_materialDiffuse + light1 + light2 + light3 + u_materialEmission; 
}