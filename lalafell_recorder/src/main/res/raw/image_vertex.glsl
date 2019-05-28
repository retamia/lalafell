precision mediump float;

attribute vec4 a_position;

attribute vec2 a_texture_uv;
varying vec2 texture_uv;

uniform mat4 u_projection;

void main()
{
    gl_Position = u_projection * a_position;
    texture_uv  = a_texture_uv;
}
