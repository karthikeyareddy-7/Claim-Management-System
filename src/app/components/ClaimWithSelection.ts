import { Claim } from './claims'; // adjust the path to where Claim is defined

export interface ClaimWithSelection extends Claim {
  selected?: boolean;
}
