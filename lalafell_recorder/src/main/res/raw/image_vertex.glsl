precision mediump float;


attribute vec2 a_vertex;
attribute vec2 a_texture_uv;
varying vec2 texture_uv;

uniform mat4 u_projection;

void main()
{
    vec4 model = vec4(a_vertex, 0, 0);
    gl_Position = u_projection * model;
    texture_uv  = a_texture_uv;
}
