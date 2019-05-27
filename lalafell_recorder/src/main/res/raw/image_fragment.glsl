precision mediump float;

uniform sampler2D u_texture_y;
uniform sampler2D u_texture_u;
uniform sampler2D u_texture_v;

varying vec2 texture_uv;

void main()
{
    mediump vec3 yuv;
    lowp vec3 rgb;

    yuv.x = texture2D(u_texture_y, texture_uv).r;
    yuv.y = texture2D(u_texture_u, texture_uv).r;
    yuv.z = texture2D(u_texture_v, texture_uv).r;


    yuv.y = yuv.y - 0.5;
    yuv.z = yuv.z - 0.5;

    rgb = mat3( 1,       1,       1,
                0, -.21482, 2.12798,
                1.28033, -.38059, 0) * yuv;

    /*
    yuv.x = 1.164 * (yuv.x - 0.0625);
    rgb.x = yuv.x + 1.596023559570 * yuv.z;
    rgb.y = yuv.x - 0.3917694091796875 * yuv.y - 0.8129730224609375 * yuv.z;
    rgb.z = yuv.x + 2.017227172851563 * yuv.y;*/

    gl_FragColor = vec4(yuv, 1);

}
