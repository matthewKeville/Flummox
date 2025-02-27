import React from 'react';

import { useMediaQuery } from '@mantine/hooks';

import { Grid, Divider, Group, em } from "@mantine/core";
import Intro from "/src/main/js/components/home/Intro.jsx";
import SiteStats from "/src/main/js/components/home/SiteStats.jsx";

export default function Home() {

  const isMobile = useMediaQuery(`(max-width: ${em(750)})`);

  if (isMobile) {

    return (
      <Intro/>
    )

  } else {

    return (
      <Grid>
        <Grid.Col span={8}>
          <Intro/>
        </Grid.Col>
        <Grid.Col span={2}>
          <Group h="100%" justify='center'>
            <Divider h="100%" orientation='vertical'/>
          </Group>
        </Grid.Col>
        <Grid.Col span={2}>
          <Group justify="flex-start">
          <SiteStats/>
          </Group>
        </Grid.Col>
      </Grid>
    )

  }
}
